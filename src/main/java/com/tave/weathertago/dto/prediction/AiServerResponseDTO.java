package com.tave.weathertago.dto.prediction;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AiServerResponseDTO {
    private String status;
    private String congestion_level;
    private double congestion_score;
    private Double total_time_sec;
}