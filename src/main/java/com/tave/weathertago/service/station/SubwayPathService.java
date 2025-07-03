package com.tave.weathertago.service.station;

import com.tave.weathertago.apiPayload.code.status.ErrorStatus;
import com.tave.weathertago.apiPayload.exception.handler.StationHandler;
import com.tave.weathertago.converter.SubwayPathConverter;
import com.tave.weathertago.domain.Station;
import com.tave.weathertago.dto.station.SubwayPathDTO;
import com.tave.weathertago.dto.station.SubwayPathResponseDTO;
import com.tave.weathertago.infrastructure.SubwayOpenApiClient;
import com.tave.weathertago.repository.StationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SubwayPathService {

    private final StationRepository stationRepository;
    private final SubwayOpenApiClient openApiClient;

    public SubwayPathDTO findPath(String start, String end) {
        Station startStation = stationRepository.findFirstByName(start)
                .orElseThrow(() -> new StationHandler(ErrorStatus.STATION_NAME_NOT_FOUND));
        Station endStation = stationRepository.findFirstByName(end)
                .orElseThrow(() -> new StationHandler(ErrorStatus.STATION_NAME_NOT_FOUND));

        // 로그로 좌표 확인
        System.out.println("출발역 좌표: " + startStation.getLatitude() + ", " + startStation.getLongitude());
        System.out.println("도착역 좌표: " + endStation.getLatitude() + ", " + endStation.getLongitude());

        SubwayPathResponseDTO response = openApiClient.getPathInfo(
                startStation.getLongitude(), startStation.getLatitude(),
                endStation.getLongitude(), endStation.getLatitude()
        );

        return SubwayPathConverter.from(response);
    }
}