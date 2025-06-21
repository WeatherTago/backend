package com.tave.weathertago.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CongestionDTO {
    private String level;  // "혼잡"
    private Integer rate;      // 85%
}