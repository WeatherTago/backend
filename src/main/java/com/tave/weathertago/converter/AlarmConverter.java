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
                .pushToken(alarm.getPushToken())
                .referenceTime(alarm.getReferenceTime())
                .stationName(alarm.getStationName())
                .stationLine(alarm.getStationLine())
                .direction(alarm.getDirection())
                .alarmDay(alarm.getAlarmDay())
                .alarmTime(alarm.getAlarmTime())
                // .weather(weatherDTO) // 필요 시 추가
                // .congestion(congestionDTO) // 필요 시 추가
                .build();
    }

    public static AlarmFcmMessageDto toAlarmFcmMessageDto(Alarm alarm, String title, String body) {
        return AlarmFcmMessageDto.builder()
                .pushToken(alarm.getPushToken())
                .title(title)
                .body(body)
                .build();
    }


}
