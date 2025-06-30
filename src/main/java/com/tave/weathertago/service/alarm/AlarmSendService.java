package com.tave.weathertago.service.alarm;

import com.tave.weathertago.dto.alarm.AlarmFcmMessageDto;

public interface AlarmSendService {
    AlarmFcmMessageDto sendAlarm(Long alarmId);
}
