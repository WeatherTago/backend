package com.tave.weathertago.service.congestion;

import com.tave.weathertago.apiPayload.code.status.ErrorStatus;
import com.tave.weathertago.apiPayload.exception.handler.CongestionHandler;
import com.tave.weathertago.apiPayload.exception.handler.StationHandler;
import com.tave.weathertago.domain.Station;
import com.tave.weathertago.dto.prediction.PredictionResponseDTO;
import com.tave.weathertago.dto.prediction.PredictionWithWeatherResponseDTO;
import com.tave.weathertago.dto.weather.WeatherResponseDTO;
import com.tave.weathertago.infrastructure.AiPredictionClient;
import com.tave.weathertago.infrastructure.WeatherApiClient;
import com.tave.weathertago.repository.StationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class CongestionQueryServiceImpl implements CongestionQueryService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final AiPredictionClient aiPredictionClient;
    private final WeatherApiClient weatherApiClient;
    private final StationRepository stationRepository;

    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @Override
    public PredictionWithWeatherResponseDTO getCongestionWithWeather(Long stationId, String direction, LocalDateTime datetime) {
        Station station = getStationOrThrow(stationId);

        PredictionResponseDTO prediction = getOrPredictCongestion(station, direction, datetime);

        WeatherResponseDTO weather = getOrFetchWeather(station, datetime);

        return PredictionWithWeatherResponseDTO.builder()
                .prediction(prediction)
                .weather(weather)
                .build();
    }

    @Override
    public PredictionResponseDTO getCongestion(Long stationId, String direction, LocalDateTime datetime) {
        Station station = getStationOrThrow(stationId);
        return getOrPredictCongestion(station, direction, datetime);
    }

    // 내부 공통 메서드들
    private PredictionResponseDTO getOrPredictCongestion(Station station, String direction, LocalDateTime datetime) {
        String key = makeCongestionRedisKey(station.getId(), direction, datetime);
        Object cached = redisTemplate.opsForValue().get(key);

        if (cached instanceof PredictionResponseDTO prediction) {
            log.info("✅ 혼잡도 Redis 캐시 Hit: {}", key);
            return prediction;
        }

        WeatherResponseDTO weather = getOrFetchWeather(station, datetime);
        return aiPredictionClient.predictCongestion(station.getId(), direction, datetime, weather);
    }

    private WeatherResponseDTO getOrFetchWeather(Station station, LocalDateTime datetime) {
        String weatherKey = makeWeatherRedisKey(station.getNx(), station.getNy(), datetime);
        Object cached = redisTemplate.opsForValue().get(weatherKey);

        if (cached instanceof WeatherResponseDTO weather) {
            log.info("✅ 날씨 Redis 캐시 Hit: {}", weatherKey);
            return weather;
        }

        log.info("날씨 Redis 캐시 Miss → 기상청 API 요청");
        return weatherApiClient.getAndCacheWeather(station.getId(), datetime);
    }

    private Station getStationOrThrow(Long stationId) {
        return stationRepository.findById(stationId)
                .orElseThrow(() -> new CongestionHandler(ErrorStatus.STATION_ID_NOT_FOUND));
    }

    private String makeCongestionRedisKey(Long stationId, String directionKor, LocalDateTime datetime) {
        int direction = switch (directionKor) {
            case "상선" -> 0;
            case "하선" -> 1;
            case "내선" -> 2;
            case "외선" -> 3;
            default -> throw new CongestionHandler(ErrorStatus.INVALID_DIRECTION);
        };
        return "congestion:" + stationId + ":" + direction + ":" + datetime.format(DATETIME_FMT);
    }

    private String makeWeatherRedisKey(Integer nx, Integer ny, LocalDateTime datetime) {
        return "weather:" + nx + ":" + ny + ":" + datetime.format(DATETIME_FMT);
    }
}