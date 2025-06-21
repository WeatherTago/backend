package com.tave.weathertago.dto.Station;

import com.tave.weathertago.dto.CongestionDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
        CongestionDTO congestion;
        LocalDateTime createdAt;
    }
}
