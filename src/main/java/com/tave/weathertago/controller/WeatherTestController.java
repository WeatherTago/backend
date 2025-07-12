package com.tave.weathertago.controller;


import com.tave.weathertago.apiPayload.ApiResponse;
import com.tave.weathertago.dto.weather.WeatherResponseDTO;
import com.tave.weathertago.service.weather.WeatherQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/test/weather")
@RequiredArgsConstructor
public class WeatherTestController {

    private final WeatherQueryService weatherQueryService;

    @GetMapping
    public ApiResponse<WeatherResponseDTO> getWeather(
            @RequestParam Long stationId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime datetime
    ) {
        WeatherResponseDTO response = weatherQueryService.getWeather(stationId, datetime);
        return ApiResponse.onSuccess(response);
    }
}