package com.tave.weathertago.dto.station;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tave.weathertago.dto.prediction.PredictionResponseDTO;
import com.tave.weathertago.dto.weather.WeatherResponseDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;

public class StationResponseDTO {

    @Getter
    @Builder
    @AllArgsConstructor
    public static class JoinResultDTO{
        Long stationId;
        String name;
        String line;
        String stationCode;
        String direction;
        WeatherResponseDTO weather;
        Map<String, DirectionalStationDTO> congestionByDirection;
        @Schema(type = "string", format = "date-time", example = "2025-07-08T22:40:00")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
        LocalDateTime createdAt;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class DirectionalStationDTO{
        Long stationId;
        private PredictionResponseDTO congestion;
    }

    @Data
    @Builder
    public static class SimpleStationDTO {
        private Long stationId;
        private String stationName;
        private String stationLine;
    }

    @Data
    @Builder
    public static class StationInfoDTO{
        private Long stationId;
        private String stationName;
        private String stationLine;
        private String phoneNumber;
        private String address;
    }
}
