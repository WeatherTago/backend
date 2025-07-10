package com.tave.weathertago.dto.prediction;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PredictionResponseDTO {
    private double congestionScore;
    private String congestionLevel;
}