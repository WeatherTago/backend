package com.tave.weathertago.service.station;

import com.tave.weathertago.apiPayload.code.status.ErrorStatus;
import com.tave.weathertago.apiPayload.exception.handler.StationHandler;
import com.tave.weathertago.converter.SubwayPathConverter;
import com.tave.weathertago.domain.Station;
import com.tave.weathertago.dto.station.SubwayPathDTO;
import com.tave.weathertago.dto.station.SubwayPathResponseDTO;
import com.tave.weathertago.infrastructure.SubwayOpenApiClient;
import com.tave.weathertago.repository.StationRepository;
import com.tave.weathertago.service.congestion.CongestionQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SubwayPathQueryServiceImpl implements SubwayPathQueryService {

    private final StationRepository stationRepository;
    private final SubwayOpenApiClient openApiClient;
    private final CongestionQueryService congestionQueryService;

    @Override
    public SubwayPathDTO findPath(Long startStationId, Long endStationId, LocalDateTime queryTime) {
        Station startStation = stationRepository.findById(startStationId)
                .orElseThrow(() -> new StationHandler(ErrorStatus.STATION_NAME_NOT_FOUND));
        Station endStation = stationRepository.findById(endStationId)
                .orElseThrow(() -> new StationHandler(ErrorStatus.STATION_NAME_NOT_FOUND));

        SubwayPathResponseDTO response = openApiClient.getPathInfo(
                startStation.getLongitude(), startStation.getLatitude(),
                endStation.getLongitude(), endStation.getLatitude()
        );

        System.out.println("출발역 좌표: " + startStation.getLatitude() + ", " + startStation.getLongitude());
        System.out.println("도착역 좌표: " + endStation.getLatitude() + ", " + endStation.getLongitude());

        return SubwayPathConverter.from(response, stationRepository, congestionQueryService, queryTime)
                .orElseThrow(() -> new StationHandler(ErrorStatus.NO_SUBWAY_ROUTE_FOUND));

    }
}
