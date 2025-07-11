package com.tave.weathertago.service.congestion;

import com.tave.weathertago.dto.prediction.PredictionResponseDTO;
import com.tave.weathertago.dto.prediction.PredictionWithWeatherResponseDTO;

import java.time.LocalDateTime;

public interface CongestionQueryService {

    // 혼잡도 + 날씨 동시 반환
    PredictionWithWeatherResponseDTO getCongestionWithWeather(Long stationId, LocalDateTime datetime);

    // 혼잡도만 반환
    PredictionResponseDTO getCongestion(Long stationId, LocalDateTime datetime);
}