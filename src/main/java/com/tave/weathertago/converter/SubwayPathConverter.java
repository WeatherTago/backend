package com.tave.weathertago.converter;

import com.tave.weathertago.dto.station.SubwayPathDTO;
import com.tave.weathertago.dto.station.SubwayPathResponseDTO;

import java.util.ArrayList;
import java.util.List;


public class SubwayPathConverter {

    public static SubwayPathDTO from(SubwayPathResponseDTO response) {
        if (response == null || response.getMsgBody() == null || response.getMsgBody().getItemList().isEmpty()) {
            throw new RuntimeException("API 응답이 비어있습니다.");
        }

        SubwayPathResponseDTO.Item item = response.getMsgBody().getItemList().get(0);

        List<SubwayPathDTO.SubwayStepDto> steps = new ArrayList<>();
        for (SubwayPathResponseDTO.PathList path : item.getPathList()) {
            steps.add(SubwayPathDTO.SubwayStepDto.builder()
                    .line(path.getRouteNm())
                    .startStation(path.getFname())
                    .endStation(path.getTname())
                    .build());
        }

        return SubwayPathDTO.builder()
                .totalTime(item.getTime())
                .totalDistance(item.getDistance())
                .steps(steps)
                .build();
    }
}
