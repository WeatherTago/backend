package com.tave.weathertago.dto.station;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class SubwayPathDTO {
    private String totalTime;
    private String totalDistance;
    private List<SubwayStepDto> steps;

    @Builder
    @Getter
    public static class SubwayStepDto {
        private String line;
        private String startStation;
        private String endStation;
    }
}
