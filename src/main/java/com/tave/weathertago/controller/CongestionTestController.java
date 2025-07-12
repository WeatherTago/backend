package com.tave.weathertago.controller;


import com.tave.weathertago.apiPayload.ApiResponse;
import com.tave.weathertago.dto.prediction.PredictionResponseDTO;
import com.tave.weathertago.dto.prediction.PredictionWithWeatherResponseDTO;
import com.tave.weathertago.service.congestion.CongestionQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/congestion")
@RequiredArgsConstructor
public class CongestionTestController {


    private final CongestionQueryService congestionQueryService;

    // 혼잡도만 조회
    @GetMapping
    public ApiResponse<PredictionResponseDTO> getCongestion(
            @RequestParam Long stationId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime datetime
    ) {
        PredictionResponseDTO result = congestionQueryService.getCongestion(stationId, datetime);
        return ApiResponse.onSuccess(result);
    }

    // 혼잡도 + 날씨 조회
    @GetMapping("/with-weather")
    public ApiResponse<PredictionWithWeatherResponseDTO> getCongestionWithWeather(
            @RequestParam Long stationId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime datetime
    ) {
        PredictionWithWeatherResponseDTO result = congestionQueryService.getCongestionWithWeather(stationId, datetime);
        return ApiResponse.onSuccess(result);
    }

    @GetMapping("/range")
    public ApiResponse<List<PredictionResponseDTO>> getCongestionRange(
            @RequestParam Long stationId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime baseDateTime
    ) {
        List<PredictionResponseDTO> results = new ArrayList<>();

        for (int hour = 1; hour <= 9; hour++) {
            LocalDateTime datetime = baseDateTime.withHour(hour).withMinute(0).withSecond(0).withNano(0);
            PredictionResponseDTO result = congestionQueryService.getCongestion(stationId, datetime);
            results.add(result);
        }

        return ApiResponse.onSuccess(results);
    }
}