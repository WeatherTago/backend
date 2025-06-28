package com.tave.weathertago.service.Station;

import com.tave.weathertago.domain.Station;

import java.util.List;

public interface StationQueryService {
    List<Station> getStationsByName(String name);

    List<Station> getAllStations();

    String getStationCodeByNameAndLine(String name, String line);
}
