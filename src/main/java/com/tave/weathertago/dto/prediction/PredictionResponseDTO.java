package com.tave.weathertago.dto.prediction;

import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
public class PredictionResponseDTO {
    private double congestionScore;
    private String congestionLevel;
}