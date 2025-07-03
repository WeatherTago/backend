package com.tave.weathertago.service.weather;

import com.tave.weathertago.dto.WeatherDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class WeatherQueryService {
    public WeatherDTO getWeather(Double lat, Double lon, LocalDateTime time) {
        // TODO: 실제 API 연동 예정
        return new WeatherDTO("맑음", "25.0");
    }
}