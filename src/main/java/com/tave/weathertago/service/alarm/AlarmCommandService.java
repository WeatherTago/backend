package com.tave.weathertago.service.alarm;

import com.tave.weathertago.dto.fcm.AlarmFcmMessageDto;

public interface AlarmCommandService {
    void sendAlarm(AlarmFcmMessageDto dto);
}
