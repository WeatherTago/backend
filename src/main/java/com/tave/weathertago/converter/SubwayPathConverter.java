package com.tave.weathertago.converter;

import com.tave.weathertago.apiPayload.code.status.ErrorStatus;
import com.tave.weathertago.apiPayload.exception.handler.StationHandler;
import com.tave.weathertago.dto.Station.SubwayPathDTO;
import com.tave.weathertago.dto.Station.SubwayPathResponseDTO;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class SubwayPathConverter {

    public static SubwayPathDTO from(SubwayPathResponseDTO response) {
        // msgBody → itemList 가져오기
        List<SubwayPathResponseDTO.Item> items =
                Optional.ofNullable(response.getMsgBody())
                        .map(SubwayPathResponseDTO.MsgBody::getItemList)
                        .orElse(Collections.emptyList());

        if (items.isEmpty()) {
            throw new StationHandler(ErrorStatus.PATH_NOT_FOUND);
        }

        // 첫 번째 item에서 거리/시간 추출
        SubwayPathResponseDTO.Item firstItem = items.get(0);

        List<SubwayPathDTO.SubwayStepDto> steps = items.stream()
                .map(item -> SubwayPathDTO.SubwayStepDto.builder()
                        .line(item.getRouteNm())
                        .startStation(item.getFname())
                        .endStation(item.getTname())
                        .build())
                .toList();

        return SubwayPathDTO.builder()
                .totalTime(firstItem.getTime())
                .totalDistance(firstItem.getDistance())
                .steps(steps)
                .build();
    }
}

