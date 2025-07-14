package com.tave.weathertago.service.station;

import com.tave.weathertago.dto.station.StationResponseDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface StationQueryService {

    StationResponseDTO.JoinResultDTO getStationById(Long stationId, LocalDateTime time);

    List<StationResponseDTO.SimpleStationDTO> getAllSimpleStations();

    List<StationResponseDTO.StationInfoDTO> getAllStationsInfo();

    Map<String, StationResponseDTO.DirectionalData> getStatus(Long stationId);
}


