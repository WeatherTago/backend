package com.tave.weathertago.dto.weather;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeatherResponseDTO {
    private double tmp;
    private double reh;
    private double pcp;
    private double wsd;
    private double sno;
    private double vec;
    private String status;
}