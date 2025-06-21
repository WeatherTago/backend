package com.tave.weathertago.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
public class TimeTableDTO {
    private String departureTime;  // "20:22"
    private String destination;    // "하남검단산"
    private String direction;      // "상행" 또는 "하행"
    private int hour;              // 20 ← 시간 필터링용
}