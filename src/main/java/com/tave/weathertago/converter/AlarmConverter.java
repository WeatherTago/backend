package com.tave.weathertago.converter;

import com.tave.weathertago.domain.Alarm;
import com.tave.weathertago.domain.User;
import com.tave.weathertago.dto.alarm.AlarmFcmMessageDto;
import com.tave.weathertago.dto.alarm.AlarmRequestDTO;
import com.tave.weathertago.dto.alarm.AlarmResponseDTO;

public class AlarmConverter {

    // Alarm 엔티티를 AlarmDetailDTO로 변환
    public static AlarmResponseDTO.AlarmDetailDTO toAlarmDetailDTO(Alarm alarm) {
        return AlarmResponseDTO.AlarmDetailDTO.builder()
                .alarmId(alarm.getAlarmId())
                .stationId(alarm.getStationId().getId())
                .stationName(alarm.getStationId().getName())
                .stationLine(alarm.getStationId().getLine())
                .direction(alarm.getStationId().getDirection())
                .referenceTime(alarm.getReferenceTime())
                .alarmPeriod((alarm.getAlarmPeriod()))
                .alarmDay(alarm.getAlarmDay())
                .alarmTime(alarm.getAlarmTime())
                // .weather(weatherDTO) // 필요 시 추가
                // .congestion(congestionDTO) // 필요 시 추가
                .build();
    }

    public static AlarmFcmMessageDto toAlarmFcmMessageDto(String title, String body) {
        return AlarmFcmMessageDto.builder()
                .title(title)
                .body(body)
                .build();
    }


}
