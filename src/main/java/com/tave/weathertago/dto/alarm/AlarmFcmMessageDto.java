package com.tave.weathertago.dto.alarm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class AlarmFcmMessageDto {
    private String title;       // 제목
    private String body;       // 본문
}
