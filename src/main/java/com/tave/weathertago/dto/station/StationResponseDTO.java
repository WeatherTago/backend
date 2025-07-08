package com.tave.weathertago.dto.station;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tave.weathertago.dto.CongestionDTO;
import com.tave.weathertago.dto.weather.WeatherResponseDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;

public class StationResponseDTO {

    @Getter
    @Builder
    @AllArgsConstructor
    public static class JoinResultDTO{
        Long stationId;
        String name;
        String line;
        String stationCode;
        WeatherResponseDTO weather;
        CongestionDTO congestion;
        @Schema(type = "string", format = "date-time", example = "2025-07-08T22:40:00")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
        LocalDateTime createdAt;
    }

    @Data
    @Builder
    public static class SimpleStationDTO {
        private Long stationId;
        private String stationName;
        private String stationLine;
    }
}
