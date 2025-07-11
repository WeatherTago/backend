package com.tave.weathertago.service.weather;

import com.tave.weathertago.apiPayload.code.status.ErrorStatus;
import com.tave.weathertago.apiPayload.exception.handler.StationHandler;
import com.tave.weathertago.domain.Station;
import com.tave.weathertago.dto.weather.WeatherResponseDTO;
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
public class WeatherQueryServiceImpl implements WeatherQueryService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final WeatherApiClient weatherApiClient;
    private final StationRepository stationRepository;

    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @Override
    public WeatherResponseDTO getWeather(Long stationId, LocalDateTime datetime) {
        Station station = stationRepository.findById(stationId)
                .orElseThrow(() -> new StationHandler(ErrorStatus.STATION_ID_NOT_FOUND));
        String key = makeWeatherRedisKey(station.getNx(), station.getNy(), datetime);

        Object cached = redisTemplate.opsForValue().get(key);
        if (cached instanceof WeatherResponseDTO weather) {
            log.info("✅ 날씨 Redis 캐시 Hit: {}", key);
            return weather;
        }

        log.info("날씨 Redis 캐시 Miss → 기상청 API 요청");
        return weatherApiClient.getAndCacheWeather(stationId, datetime);
    }

    private String makeWeatherRedisKey(Integer nx, Integer ny, LocalDateTime datetime) {
        return "weather:" + nx + ":" + ny + ":" + datetime.format(DATETIME_FMT);
    }
}