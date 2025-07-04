package com.tave.weathertago.service.station;

import com.tave.weathertago.dto.station.StationResponseDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface StationQueryService {
    StationResponseDTO.JoinResultDTO getStationByNameAndLine(String name, String line, LocalDateTime queryTime);


    List<StationResponseDTO.SimpleStationDTO> getAllSimpleStations();

}
    /*
    String getStationCodeByNameAndLine(String name, String line);
     */

