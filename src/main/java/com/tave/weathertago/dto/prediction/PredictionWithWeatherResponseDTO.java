package com.tave.weathertago.dto.prediction;

import com.tave.weathertago.dto.weather.WeatherResponseDTO;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PredictionWithWeatherResponseDTO {
    private PredictionResponseDTO prediction;
    private WeatherResponseDTO weather;
}