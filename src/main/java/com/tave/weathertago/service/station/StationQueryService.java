package com.tave.weathertago.service.station;

import com.tave.weathertago.dto.station.StationResponseDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface StationQueryService {
    List<StationResponseDTO.JoinResultDTO> getStationsByName(String name, LocalDateTime time);

    List<StationResponseDTO.JoinResultDTO> getAllStations(LocalDateTime time);

    StationResponseDTO.JoinResultDTO getStationByNameAndLine(String name, String line, LocalDateTime time);



    /*
    String getStationCodeByNameAndLine(String name, String line);
     */
}
