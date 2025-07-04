package com.tave.weathertago.dto.alarm;

import com.tave.weathertago.domain.AlarmDay;
import com.tave.weathertago.domain.AlarmPeriod;
import com.tave.weathertago.domain.Direction;
import com.tave.weathertago.domain.Station;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;

public class AlarmRequestDTO {

    @Getter
    @Builder
    public static class AlarmCreateRequestDTO {
        private String pushToken;

        private Long stationId;
        private Direction direction;

        private LocalTime referenceTime;
        private AlarmPeriod alarmPeriod;
        private AlarmDay alarmDay;
        private LocalTime alarmTime;
    }

    @Getter
    @Builder
    public static class AlarmUpdateRequestDTO {
        private Long alarmId;
        private String pushToken;

        private Long stationId;
        private Direction direction;

        private LocalTime referenceTime;
        private AlarmPeriod alarmPeriod;
        private AlarmDay alarmDay;
        private LocalTime alarmTime;
    }
}
