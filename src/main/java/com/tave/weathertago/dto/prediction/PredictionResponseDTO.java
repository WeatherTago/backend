package com.tave.weathertago.dto.prediction;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PredictionResponseDTO {
    private String line;
    private String stationName;
    private LocalDateTime datetime;
    private String predictedCongestionLevel;
    private double predictedCongestionScore;
}