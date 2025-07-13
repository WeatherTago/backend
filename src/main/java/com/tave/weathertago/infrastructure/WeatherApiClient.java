package com.tave.weathertago.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tave.weathertago.apiPayload.code.status.ErrorStatus;
import com.tave.weathertago.apiPayload.exception.handler.WeatherHandler;
import com.tave.weathertago.domain.Station;
import com.tave.weathertago.dto.weather.WeatherApiResponseDTO;
import com.tave.weathertago.dto.weather.WeatherInternalDTO;
import com.tave.weathertago.repository.StationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class WeatherApiClient {

    private final StationRepository stationRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    @Value("${weather.api.key}")
    private String serviceKey;

    private static final String API_BASE_URL = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst";
    private static final Duration TTL = Duration.ofHours(3);
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HHmm");
    private static final DateTimeFormatter DATETIME_KEY_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public WeatherInternalDTO getAndCacheWeather(Long stationId, LocalDateTime datetime) {
        Station station = stationRepository.findById(stationId)
                .orElseThrow(() -> new WeatherHandler(ErrorStatus.STATION_ID_NOT_FOUND));

        LocalDateTime baseTime = calculateBaseTime(LocalDateTime.now());
        URI uri = buildRequestUri(station, baseTime);

        String responseBody = sendApiRequest(uri);
        WeatherApiResponseDTO apiResponse = parseWeatherResponse(responseBody);
        Map<String, WeatherInternalDTO> cacheMap = processApiResponse(station, apiResponse);

        bulkSaveToRedis(cacheMap);

        String redisKey = makeRedisKey(station.getNx(), station.getNy(), datetime);
        WeatherInternalDTO result = cacheMap.get(redisKey);

        // 정확히 일치하는 예보 시간이 없을 경우: 가장 가까운 예보 시간으로 fallback
        if (result == null && !cacheMap.isEmpty()) {
            result = getNearestForecast(cacheMap, station, datetime);
            log.warn("⚠ 요청 시간 {}에 대한 예보가 없어 fallback 예보 사용", datetime);
        }

        return result;
    }

    private WeatherInternalDTO getNearestForecast(Map<String, WeatherInternalDTO> cacheMap, Station station, LocalDateTime targetTime) {
        return cacheMap.entrySet().stream()
                .min(Comparator.comparing(entry -> {
                    String keyTime = entry.getKey().split(":")[3]; // yyyy-MM-dd'T'HH:mm:ss
                    LocalDateTime forecastTime = LocalDateTime.parse(keyTime, DATETIME_KEY_FMT);
                    return Duration.between(forecastTime, targetTime).abs();
                }))
                .map(Map.Entry::getValue)
                .orElse(null);
    }

    private URI buildRequestUri(Station station, LocalDateTime baseTime) {
        String encodedKey = URLEncoder.encode(serviceKey, StandardCharsets.UTF_8);
        return UriComponentsBuilder.fromUriString(API_BASE_URL)
                .queryParam("serviceKey", encodedKey)
                .queryParam("pageNo", 1)
                .queryParam("numOfRows", 1000)
                .queryParam("dataType", "JSON")
                .queryParam("base_date", baseTime.format(DATE_FMT))
                .queryParam("base_time", baseTime.format(TIME_FMT))
                .queryParam("nx", station.getNx())
                .queryParam("ny", station.getNy())
                .build(true)
                .toUri();

    }

    private String sendApiRequest(URI uri) {
        try {
            ResponseEntity<String> responseEntity = restClient.get()
                    .uri(uri)
                    .retrieve()
                    .toEntity(String.class);

            String body = responseEntity.getBody();
            if (body == null || body.isEmpty()) {
                throw new WeatherHandler(ErrorStatus.WEATHER_API_RESPONSE_EMPTY);
            }

            log.info("🌐 기상청 API 요청 URI: {}", uri);
            log.info("📄 Raw Response (일부): {}", body.substring(0, Math.min(200, body.length())));
            return body;

        } catch (Exception e) {
            throw new WeatherHandler(ErrorStatus.WEATHER_API_FAIL);
        }
    }

    private WeatherApiResponseDTO parseWeatherResponse(String body) {
        try {
            return objectMapper.readValue(body, WeatherApiResponseDTO.class);
        } catch (Exception e) {
            throw new WeatherHandler(ErrorStatus.WEATHER_API_PARSE_ERROR);
        }
    }

    private Map<String, WeatherInternalDTO> processApiResponse(Station station, WeatherApiResponseDTO apiResponse) {
        if (apiResponse == null ||
                apiResponse.getResponse() == null ||
                apiResponse.getResponse().getBody() == null ||
                apiResponse.getResponse().getBody().getItems() == null ||
                apiResponse.getResponse().getBody().getItems().getItem() == null) {
            throw new WeatherHandler(ErrorStatus.WEATHER_API_INVALID_STRUCTURE);
        }

        String resultCode = apiResponse.getResponse().getHeader().getResultCode();
        if (!"00".equals(resultCode)) {
            throw new WeatherHandler(ErrorStatus.WEATHER_API_FAIL);
        }

        Map<String, Map<String, String>> grouped = new HashMap<>();
        for (WeatherApiResponseDTO.Item item : apiResponse.getResponse().getBody().getItems().getItem()) {
            String key = item.getFcstDate() + item.getFcstTime();
            grouped.computeIfAbsent(key, k -> new HashMap<>())
                    .put(item.getCategory(), item.getFcstValue());
        }

        Map<String, WeatherInternalDTO> cacheMap = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();

        for (Map.Entry<String, Map<String, String>> entry : grouped.entrySet()) {
            LocalDateTime fcstTime = LocalDateTime.parse(entry.getKey(), DateTimeFormatter.ofPattern("yyyyMMddHHmm"));

            if (fcstTime.getHour() >= 1 && fcstTime.getHour() <= 4) continue;
            if (fcstTime.isAfter(now.toLocalDate().plusDays(3).atTime(0, 0))) continue;

            Map<String, String> cat = entry.getValue();
            WeatherInternalDTO dto = WeatherInternalDTO.builder()
                    .tmp(parse("TMP", cat.get("TMP")))
                    .reh(parse("REH", cat.get("REH")))
                    .pcp(parse("PCP", cat.get("PCP")))
                    .wsd(parse("WSD", cat.get("WSD")))
                    .sno(parse("SNO", cat.get("SNO")))
                    .vec(parse("VEC", cat.get("VEC")))
                    .sky(parseInt(cat.get("SKY")))
                    .pty(parseInt(cat.get("PTY")))
                    .build();

            String redisKey = makeRedisKey(station.getNx(), station.getNy(), fcstTime);
            cacheMap.put(redisKey, dto);
        }

        return cacheMap;
    }

    private void bulkSaveToRedis(Map<String, WeatherInternalDTO> dataMap) {
        RedisSerializer<String> keySerializer = redisTemplate.getStringSerializer();
        RedisSerializer<Object> valueSerializer = (RedisSerializer<Object>) redisTemplate.getValueSerializer();

        redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            for (Map.Entry<String, WeatherInternalDTO> entry : dataMap.entrySet()) {
                byte[] key = keySerializer.serialize(entry.getKey());
                byte[] value = valueSerializer.serialize(entry.getValue());
                connection.stringCommands().set(
                        key,
                        value,
                        Expiration.seconds(TTL.getSeconds()),
                        RedisStringCommands.SetOption.UPSERT
                );
            }
            return null;
        });
    }

    private double parse(String category, String val) {
        if (val == null || val.equals("0") || val.equals("강수없음") || val.equals("적설없음")) {
            return 0.0;
        }

        try {
            switch (category) {
                case "PCP":
                    if (val.contains("mm 미만")) return 1.0;
                    if (val.contains("mm 이상")) return 50.0;
                    if (val.contains("~")) {
                        String[] parts = val.replace("mm", "").split("~");
                        return Double.parseDouble(parts[1]);
                    }
                    return Double.parseDouble(val.replace("mm", ""));

                case "SNO":
                    if (val.contains("0.5cm 미만")) return 0.5;
                    if (val.contains("cm 이상")) return 5.0;
                    if (val.contains("~")) {
                        String[] parts = val.replace("cm", "").split("~");
                        return Double.parseDouble(parts[1]);
                    }
                    return Double.parseDouble(val.replace("cm", ""));

                default:
                    return Double.parseDouble(val);
            }
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private int parseInt(String val) {
        if (val == null) return 0;
        try {
            return Integer.parseInt(val);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private String makeRedisKey(Integer nx, Integer ny, LocalDateTime time) {
        return "weather:" + nx + ":" + ny + ":" + time.format(DATETIME_KEY_FMT);
    }

    private LocalDateTime calculateBaseTime(LocalDateTime now) {
        int[][] timeSlots = {
                {2, 10}, {5, 10}, {8, 10}, {11, 10},
                {14, 10}, {17, 10}, {20, 10}, {23, 10}
        };

        for (int i = 0; i < timeSlots.length; i++) {
            int hour = timeSlots[i][0];
            int minute = timeSlots[i][1];
            LocalDateTime availableTime = now.withHour(hour).withMinute(minute);
            if (now.isBefore(availableTime)) {
                return (i == 0) ? now.minusDays(1).withHour(23).withMinute(0) :
                        now.withHour(timeSlots[i - 1][0]).withMinute(0);
            }
        }
        return now.withHour(23).withMinute(0);
    }
}
