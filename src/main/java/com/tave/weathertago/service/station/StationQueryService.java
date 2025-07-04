package com.tave.weathertago.service.station;

import com.tave.weathertago.dto.station.StationResponseDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface StationQueryService {

    StationResponseDTO.JoinResultDTO getStationById(Long stationId, LocalDateTime time);

    List<StationResponseDTO.SimpleStationDTO> getAllSimpleStations();


}


