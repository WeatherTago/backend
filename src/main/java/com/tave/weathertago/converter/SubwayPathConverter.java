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

        List<SubwayPathDTO.SubwayStepDto> steps = new ArrayList<>();
        for (SubwayPathResponseDTO.Item item : response.getMsgBody().getItemList()) {
            steps.add(SubwayPathDTO.SubwayStepDto.builder()
                    .line(item.getRouteNm())
                    .startStation(item.getFname())
                    .endStation(item.getTname())
                    .build());
        }

        // 첫 번째 item 기준으로 totalTime, totalDistance 설정
        SubwayPathResponseDTO.Item first = response.getMsgBody().getItemList().get(0);

        return SubwayPathDTO.builder()
                .totalTime(first.getTime())
                .totalDistance(first.getDistance())
                .steps(steps)
                .build();
    }

}
