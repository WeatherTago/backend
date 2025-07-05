package com.tave.weathertago.dto.prediction;

import lombok.Getter;

@Getter
public class AiServerResponseDTO {
    private String status;
    private String congestion_level;
    private double congestion_score;
}