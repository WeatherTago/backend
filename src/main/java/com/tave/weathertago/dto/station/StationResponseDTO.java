package com.tave.weathertago.dto.station;

import com.tave.weathertago.dto.CongestionDTO;
import com.tave.weathertago.dto.WeatherDTO;
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
        WeatherDTO weather;
        CongestionDTO congestion;
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
