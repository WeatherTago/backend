package com.tave.weathertago.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tave.weathertago.apiPayload.code.status.ErrorStatus;
import com.tave.weathertago.apiPayload.exception.handler.WeatherHandler;
import com.tave.weathertago.converter.PredictionConverter;
import com.tave.weathertago.domain.Station;
import com.tave.weathertago.dto.prediction.AiServerResponseDTO;
import com.tave.weathertago.dto.prediction.PredictionRequestDTO;
import com.tave.weathertago.dto.prediction.PredictionResponseDTO;
import com.tave.weathertago.dto.weather.WeatherResponseDTO;
import com.tave.weathertago.repository.StationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
@RequiredArgsConstructor
public class AiPredictionClient {

    private final RedisTemplate<String, Object> redisTemplate;
    private final WeatherApiClient weatherApiClient;
    private final StationRepository stationRepository;
    private final RestClient restClient;

    @Value("${ai.server.url}")
    private String aiServerUrl;

    private static final Duration TTL = Duration.ofHours(3);
    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public PredictionResponseDTO predictCongestion(String line, String stationName, LocalDateTime datetime) {
        Station station = stationRepository.findByNameAndLine(stationName, line)
                .orElseThrow(() -> new WeatherHandler(ErrorStatus.STATION_NOT_FOUND));

        String weatherKey = makeWeatherKey(station.getNx(), station.getNy(), datetime);
        Object weatherObj = redisTemplate.opsForValue().get(weatherKey);

        if (weatherObj == null) {
            log.info("🌧️ 날씨 캐시 없음 → 기상청 API 호출");
            weatherApiClient.getAndCacheWeather(stationName, line);
            weatherObj = redisTemplate.opsForValue().get(weatherKey);
        }

        if (!(weatherObj instanceof WeatherResponseDTO weather)) {
            throw new WeatherHandler(ErrorStatus.WEATHER_API_RESPONSE_EMPTY);
        }

        PredictionRequestDTO request = PredictionRequestDTO.builder()
                .line(line)
                .station_name(stationName)
                .datetime(datetime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .TMP(weather.getTmp())
                .REH(weather.getReh())
                .PCP(weather.getPcp())
                .WSD(weather.getWsd())
                .SNO(weather.getSno())
                .VEC(weather.getVec())
                .build();

        try {
            AiServerResponseDTO aiResponse = restClient.post()
                    .uri(aiServerUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(AiServerResponseDTO.class);

            if (!"ok".equalsIgnoreCase(aiResponse.getStatus())) {
                throw new WeatherHandler(ErrorStatus.AI_PREDICTION_FAIL);
            }

            PredictionResponseDTO prediction = PredictionConverter.toPredictionResponse(
                    line, stationName, datetime,
                    aiResponse.getCongestion_level(),
                    aiResponse.getCongestion_score()
            );

            String congestionKey = makeCongestionKey(stationName, line, datetime);
            redisTemplate.opsForValue().set(congestionKey, prediction, TTL);
            log.info("📊 혼잡도 예측 완료 및 저장: {}", congestionKey);

            return prediction;

        } catch (Exception e) {
            log.error("❌ AI 예측 실패", e);
            throw new WeatherHandler(ErrorStatus.AI_PREDICTION_FAIL);
        }
    }

    private String makeCongestionKey(String stationName, String line, LocalDateTime datetime) {
        return "congestion:" + stationName + ":" + line + ":" + datetime.format(DATETIME_FMT);
    }

    private String makeWeatherKey(Integer nx, Integer ny, LocalDateTime datetime) {
        return "weather:" + nx + ":" + ny + ":" + datetime.format(DATETIME_FMT);
    }
}