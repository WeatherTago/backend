package com.tave.weathertago.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
public class WeatherDTO {
    private String temperature;
    private String condition;
}

