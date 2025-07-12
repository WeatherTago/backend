package com.tave.weathertago.dto.prediction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PredictionResponseDTO {
    private double congestionScore;
    private String congestionLevel;
}