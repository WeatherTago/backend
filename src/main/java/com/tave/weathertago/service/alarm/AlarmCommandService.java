package com.tave.weathertago.service.alarm;

import com.tave.weathertago.dto.alarm.AlarmFcmMessageDto;
import com.tave.weathertago.dto.alarm.AlarmRequestDTO;

public interface AlarmCommandService {
    void sendAlarm(AlarmFcmMessageDto dto);

    Long createAlarm(Long userId, AlarmRequestDTO.AlarmCreateRequestDTO dto);
    void updateAlarm(Long alarmId, AlarmRequestDTO.AlarmCreateRequestDTO dto);
    void sendAlarm();
    void deleteAlarm(Long alarmId);
}
