package com.tave.weathertago.service.alarm;

import com.tave.weathertago.dto.alarm.AlarmFcmMessageDto;
import com.tave.weathertago.dto.alarm.AlarmRequestDTO;

public interface AlarmCommandService {
    Long createAlarm(AlarmRequestDTO.AlarmCreateRequestDTO dto);
    void updateAlarm(AlarmRequestDTO.AlarmUpdateRequestDTO dto);
    AlarmFcmMessageDto sendAlarm(Long alarmId);
    void deleteAlarm(Long alarmId);
}
