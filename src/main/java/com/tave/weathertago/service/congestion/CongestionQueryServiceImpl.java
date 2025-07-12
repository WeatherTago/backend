package com.tave.weathertago.service.congestion;

import com.tave.weathertago.apiPayload.code.status.ErrorStatus;
import com.tave.weathertago.apiPayload.exception.handler.CongestionHandler;
import com.tave.weathertago.apiPayload.exception.handler.StationHandler;
import com.tave.weathertago.domain.Station;
import com.tave.weathertago.dto.prediction.PredictionResponseDTO;
import com.tave.weathertago.dto.prediction.PredictionWithWeatherResponseDTO;
import com.tave.weathertago.dto.weather.WeatherResponseDTO;
import com.tave.weathertago.infrastructure.AiPredictionClient;
import com.tave.weathertago.repository.StationRepository;
import com.tave.weathertago.service.weather.WeatherQueryService;
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
    private final WeatherQueryService weatherQueryService;
    private final StationRepository stationRepository;

    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @Override
    public PredictionWithWeatherResponseDTO getCongestionWithWeather(Long stationId, LocalDateTime datetime) {
        Station station = getStationOrThrow(stationId);

        PredictionResponseDTO prediction = getOrPredictCongestion(station, datetime);
        WeatherResponseDTO weather = weatherQueryService.getWeather(stationId, datetime);

        return PredictionWithWeatherResponseDTO.builder()
                .prediction(prediction)
                .weather(weather)
                .build();
    }

    @Override
    public PredictionResponseDTO getCongestion(Long stationId, LocalDateTime datetime) {
        Station station = getStationOrThrow(stationId);

        return getOrPredictCongestion(station, datetime);
    }

    // 내부 공통 메서드
    private PredictionResponseDTO getOrPredictCongestion(Station station, LocalDateTime datetime) {
        int direction = convertDirectionToInt(station.getDirection());
        String key = makeCongestionRedisKey(station.getId(), direction, datetime);

        Object cached = redisTemplate.opsForValue().get(key);

        if (cached instanceof PredictionResponseDTO prediction) {
            log.info("✅ 혼잡도 Redis 캐시 Hit: {}", key);
            return prediction;
        }

        log.info("혼잡도 캐시 MISS → key: {}", key);
        WeatherResponseDTO weather = weatherQueryService.getWeather(station.getId(), datetime);
        return aiPredictionClient.predictCongestion(station.getId(), direction, datetime, weather);
    }

    private Station getStationOrThrow(Long stationId) {
        return stationRepository.findById(stationId)
                .orElseThrow(() -> new StationHandler(ErrorStatus.STATION_ID_NOT_FOUND));
    }

    private String makeCongestionRedisKey(Long stationId, int direction, LocalDateTime datetime) {
        return "congestion:" + stationId + ":" + direction + ":" + datetime.format(DATETIME_FMT);
    }

    private int convertDirectionToInt(String directionKor) {
        return switch (directionKor) {
            case "상행" -> 0;
            case "하행" -> 1;
            case "내선" -> 2;
            case "외선" -> 3;
            default -> throw new CongestionHandler(ErrorStatus.INVALID_DIRECTION);
        };
    }
}