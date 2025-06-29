package com.tave.weathertago.dto.alarm;

import com.tave.weathertago.domain.AlarmDay;
import com.tave.weathertago.domain.Station;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;

public class AlarmRequestDTO {

    @Getter
    @Builder
    public static class AlarmCreateRequestDTO {
        private String pushToken;
        private LocalTime referenceTime;
        private Station stationName;
        private AlarmDay alarmDay;
        private LocalTime alarmTime;
    }
}
