package com.tave.weathertago.dto.alarm;

import com.tave.weathertago.dto.CongestionDTO;
import com.tave.weathertago.dto.WeatherDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class AlarmFcmMessageDto {
    private String pushToken;     // 알림 토큰 (FCM 토큰)
    private String title;       // 제목
    private String body;       // 본문
}
