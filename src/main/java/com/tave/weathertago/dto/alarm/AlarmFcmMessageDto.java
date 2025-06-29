package com.tave.weathertago.dto.alarm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class AlarmFcmMessageDto {
    private String pushToken;     // 알림 토큰 (FCM 토큰)
    private LocalDateTime time;   // 시간
    private String day;           // 요일
    private String content;       // 내용
    private String station;       // 역
    private String weather;       // 날씨
}
