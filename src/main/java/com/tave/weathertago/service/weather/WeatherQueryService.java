package com.tave.weathertago.service.weather;

import com.tave.weathertago.dto.weather.WeatherResponseDTO;

import java.time.LocalDateTime;

public interface WeatherQueryService {
    WeatherResponseDTO getWeather(Long stationId, LocalDateTime datetime);
}