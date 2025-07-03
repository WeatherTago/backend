package com.tave.weathertago.converter;

import com.tave.weathertago.domain.Station;
import com.tave.weathertago.dto.TimeTableDTO;
import com.tave.weathertago.dto.WeatherDTO;

import java.time.LocalDateTime;
import java.util.List;

/*
public class StationDetailConverter {

    public static StationDetailResponseDTO.Response toResponse(
            Station station,
            WeatherDTO weather,
            List<TimeTableDTO> up,
            List<TimeTableDTO> down
    ) {
        return StationDetailResponseDTO.Response.builder()
                .name(station.getName())
                .line(station.getLine())
                .weather(weather)
                .upTimeTable(up)
                .downTimeTable(down)
                .createdAt(LocalDateTime.now())
                .build();
    }
}

 */