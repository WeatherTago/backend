package com.tave.weathertago.infrastructure;

import com.tave.weathertago.apiPayload.code.status.ErrorStatus;
import com.tave.weathertago.apiPayload.exception.handler.CongestionHandler;
import com.tave.weathertago.apiPayload.exception.handler.StationHandler;
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

    public PredictionResponseDTO predictCongestion(Long stationId, String direction, LocalDateTime datetime, WeatherResponseDTO weather) {
        Station station = stationRepository.findById(stationId)
                .orElseThrow(() -> new StationHandler(ErrorStatus.STATION_ID_NOT_FOUND));

        int directionNum = convertDirection(direction);

        PredictionRequestDTO request = PredictionConverter.toPredictionRequest(station, directionNum, datetime, weather);

        try {
            AiServerResponseDTO aiResponse = restClient.post()
                    .uri(aiServerUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(AiServerResponseDTO.class);

            if (!"ok".equalsIgnoreCase(aiResponse.getStatus())) {
                throw new CongestionHandler(ErrorStatus.AI_PREDICTION_FAIL);
            }

            PredictionResponseDTO prediction = PredictionConverter.toPredictionResponse(aiResponse);

            String redisKey = makeRedisKey(stationId, directionNum, datetime);
            redisTemplate.opsForValue().set(redisKey, prediction, TTL);

            log.info("✅ 혼잡도 예측 결과 저장: key={}, value={}", redisKey, prediction);
            return prediction;

        } catch (Exception e) {
            log.error("AI 예측 실패", e);
            throw new CongestionHandler(ErrorStatus.AI_PREDICTION_FAIL);
        }
    }

    private int convertDirection(String directionKor) {
        return switch (directionKor) {
            case "상선" -> 0;
            case "하선" -> 1;
            case "내선" -> 2;
            case "외선" -> 3;
            default -> throw new StationHandler(ErrorStatus.INVALID_DIRECTION);
        };
    }

    private String makeRedisKey(Long stationId, int direction, LocalDateTime datetime) {
        return "congestion:" + stationId + ":" + direction + ":" + datetime.format(DATETIME_FMT);
    }

}