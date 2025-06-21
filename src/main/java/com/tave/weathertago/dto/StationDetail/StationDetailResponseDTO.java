package com.tave.weathertago.dto.StationDetail;

import com.tave.weathertago.dto.TimeTableDTO;
import com.tave.weathertago.dto.WeatherDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

public class StationDetailResponseDTO {

    @Builder
    @Getter
    @AllArgsConstructor
    public static class Response {
        String name;
        String line;
        WeatherDTO weather;
        List<TimeTableDTO> upTimeTable;
        List<TimeTableDTO> downTimeTable;
        LocalDateTime createdAt;
    }
}
