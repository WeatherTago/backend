package com.tave.weathertago.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class WeatherDTO {
    private String temperature;
    private String condition;
}

