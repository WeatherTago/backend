package com.tave.weathertago.service.station;

import com.tave.weathertago.dto.station.SubwayPathDTO;

public interface SubwayPathQueryService {
    SubwayPathDTO findPath(String start, String end);
}
