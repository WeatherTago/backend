package com.tave.weathertago.service.alarm;

import com.tave.weathertago.dto.alarm.AlarmResponseDTO;

import java.util.List;
import java.util.Optional;

public interface AlarmQueryService {
    Optional<AlarmResponseDTO.AlarmDetailDTO> getAlarmDetail(Long alarmId);
    List<AlarmResponseDTO.AlarmDetailDTO> getAlarmsByUserId(Long userId);
}
