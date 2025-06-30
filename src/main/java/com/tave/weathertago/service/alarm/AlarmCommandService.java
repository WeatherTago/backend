package com.tave.weathertago.service.alarm;

import com.tave.weathertago.dto.alarm.AlarmFcmMessageDto;
import com.tave.weathertago.dto.alarm.AlarmRequestDTO;
import com.tave.weathertago.dto.alarm.AlarmResponseDTO;

import java.util.Optional;

public interface AlarmCommandService {
    Optional <AlarmResponseDTO.AlarmDetailDTO> createAlarm(AlarmRequestDTO.AlarmCreateRequestDTO dto);
    void updateAlarm(AlarmRequestDTO.AlarmUpdateRequestDTO dto);
    AlarmFcmMessageDto sendAlarm(Long alarmId);
    void deleteAlarm(Long alarmId);
}
