package com.tave.weathertago.service.station;

import com.tave.weathertago.dto.station.SubwayPathDTO;

import java.time.LocalDateTime;

public interface SubwayPathQueryService {
    SubwayPathDTO findPath(Long startStationId,  Long endStationId, LocalDateTime queryTime);
}
