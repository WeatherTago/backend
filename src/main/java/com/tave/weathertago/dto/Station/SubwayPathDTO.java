package com.tave.weathertago.dto.Station;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class SubwayPathDTO {
    private String totalTime;        // 전체 소요 시간
    private String totalDistance;    // 전체 이동 거리
    private List<SubwayStepDto> steps; // 경유하는 지하철 노선들

    @Builder
    @Getter
    public static class SubwayStepDto {
        private String line;         // 호선 이름 (예: 2호선)
        private String startStation; // 탑승역
        private String endStation;   // 하차역
    }
}