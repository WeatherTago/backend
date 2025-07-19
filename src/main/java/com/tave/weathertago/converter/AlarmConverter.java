package com.tave.weathertago.converter;

import com.tave.weathertago.domain.Alarm;
import com.tave.weathertago.domain.User;
import com.tave.weathertago.dto.alarm.AlarmFcmMessageDto;
import com.tave.weathertago.dto.alarm.AlarmRequestDTO;
import com.tave.weathertago.dto.alarm.AlarmResponseDTO;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;


public class AlarmConverter {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    // Alarm 엔티티를 AlarmDetailDTO로 변환
    public static AlarmResponseDTO.AlarmDetailDTO toAlarmDetailDTO(Alarm alarm) {

        return AlarmResponseDTO.AlarmDetailDTO.builder()
                .alarmId(alarm.getAlarmId())
                .stationName(alarm.getStationId().getName())
                .stationLine(alarm.getStationId().getLine())
                .direction(alarm.getStationId().getDirection())
                .referenceTime(alarm.getReferenceTime() != null ? alarm.getReferenceTime().format(TIME_FORMATTER) : null)
                .alarmPeriod(alarm.getAlarmPeriod())
                .alarmDay(alarm.getAlarmDay())
                .alarmTime(alarm.getAlarmTime() != null ? alarm.getAlarmTime().format(TIME_FORMATTER) : null)
                .build();
    }


    public static AlarmFcmMessageDto toAlarmFcmMessageDto(String title, String body) {
        return AlarmFcmMessageDto.builder()
                .title(title)
                .body(body)
                .build();
    }


}
