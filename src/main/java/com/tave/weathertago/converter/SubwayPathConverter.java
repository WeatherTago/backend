package com.tave.weathertago.converter;

import com.tave.weathertago.dto.station.SubwayPathDTO;
import com.tave.weathertago.dto.station.SubwayPathResponseDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class SubwayPathConverter {

    public static Optional<SubwayPathDTO> from(SubwayPathResponseDTO response) {
        if (response == null || response.getMsgBody() == null || response.getMsgBody().getItemList().isEmpty()) {
            return Optional.empty();
        }

        SubwayPathResponseDTO.Item firstItem = response.getMsgBody().getItemList().get(0);
        List<SubwayPathDTO.SubwayStepDto> steps = new ArrayList<>();

        for (SubwayPathResponseDTO.Path path : firstItem.getPathList()) {
            SubwayPathDTO.StationInfo start = SubwayPathDTO.StationInfo.builder()
                    .stationId(path.getFid())
                    .stationName(path.getFname())
                    .line(path.getRouteNm())
                    .build();

            SubwayPathDTO.StationInfo end = SubwayPathDTO.StationInfo.builder()
                    .stationId(path.getTid())
                    .stationName(path.getTname())
                    .line(path.getRouteNm())
                    .build();

            List<SubwayPathDTO.StationInfo> stations = new ArrayList<>();
            stations.add(start);
            stations.add(end);

            steps.add(SubwayPathDTO.SubwayStepDto.builder()
                    .line(path.getRouteNm())
                    .startStation(start)
                    .endStation(end)
                    .stations(stations)
                    .build());
        }

        return Optional.of(SubwayPathDTO.builder()
                .totalTime(firstItem.getTime())
                .totalDistance(firstItem.getDistance())
                .steps(steps)
                .build());
    }
}
