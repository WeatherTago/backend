package com.tave.weathertago.dto.station;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Getter
public class SubwayPathDTO {
    private String totalTime;         // 전체 소요 시간
    private String totalDistance;     // 전체 이동 거리
    private List<SubwayStepDto> steps; // 경유하는 지하철 구간들

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SubwayStepDto {
        private String line;                   // 대표 호선 (예: 2호선)
        private StationInfo startStation;      // 탑승역 정보 (ID, 이름, 호선)
        private StationInfo endStation;        // 하차역 정보 (ID, 이름, 호선)
        private List<StationInfo> stations;    // 전체 경유 역들 (탑승~하차 포함)
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StationInfo {
        private String stationId;     // 역 ID (예: 02220)
        private String stationName;   // 역 이름 (예: 강남역)
        private String line;          // 호선 이름 (예: 2호선)
    }
}