package com.tave.weathertago.converter;

import com.tave.weathertago.domain.Station;
import com.tave.weathertago.dto.CongestionDTO;
import com.tave.weathertago.dto.station.StationDTO;
import com.tave.weathertago.dto.station.StationResponseDTO;
import com.tave.weathertago.dto.WeatherDTO;

import java.time.LocalDateTime;

public class StationConverter {

    public static StationResponseDTO.JoinResultDTO toJoinResultDTO(Station station, WeatherDTO weather, CongestionDTO congestion) {

        return StationResponseDTO.JoinResultDTO.builder()
                .stationId(station.getId())
                .name(station.getName())
                .line(station.getLine())
                .stationCode(station.getStationCode())
                .weather(weather)
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
                .latitude(stationDTO.getLatitude())
                .longitude(stationDTO.getLongitude())
                .build();
    }

    public static StationResponseDTO.SimpleStationDTO toSimpleDTO(Station station) {
        return StationResponseDTO.SimpleStationDTO.builder()
                .stationId(station.getId())
                .stationName(station.getName())
                .stationLine(station.getLine())
                .build();
    }
}
