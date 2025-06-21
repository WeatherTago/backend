package com.tave.weathertago.converter;

import com.tave.weathertago.domain.Station;
import com.tave.weathertago.dto.CongestionDTO;
import com.tave.weathertago.dto.Station.StationDTO;
import com.tave.weathertago.dto.Station.StationResponseDTO;

import java.time.LocalDateTime;

public class StationConverter {

    public static StationResponseDTO.JoinResultDTO toJoinResultDTO(Station station){
        return StationResponseDTO.JoinResultDTO.builder()
                .stationId(station.getId())
                .name(station.getName())
                .line(station.getLine())
                .stationCode(station.getStationCode())
                .congestion(
                        new CongestionDTO(
                                station.getCongestionLevel(),
                                station.getCongestionRate()
                        )
                )
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static Station toStation(StationDTO stationDTO){

        return Station.builder()
                .name(stationDTO.getName())
                .line(stationDTO.getLine())
                .stationCode(stationDTO.getStationCode())
                .congestionLevel(stationDTO.getCongestionLevel())
                .congestionRate(stationDTO.getCongestionRate())
                .build();
    }
}
