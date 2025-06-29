package com.tave.weathertago.service.alarm;

import com.tave.weathertago.dto.alarm.AlarmFcmMessageDto;

public interface AlarmCommandService {
    void sendAlarm(AlarmFcmMessageDto dto);
}
