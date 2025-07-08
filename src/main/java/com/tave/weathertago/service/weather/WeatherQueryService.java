package com.tave.weathertago.service.weather;

import com.tave.weathertago.dto.weather.WeatherResponseDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class WeatherQueryService {
    public WeatherResponseDTO getWeather(Double lat, Double lon, LocalDateTime time) {
        // TODO: 실제 API 연동 예정
        return new WeatherResponseDTO(32.6,61,10,1.7,1,1);
    }
}