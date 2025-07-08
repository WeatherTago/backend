package com.tave.weathertago.dto.alarm;

import com.tave.weathertago.domain.*;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;

public class AlarmResponseDTO {

    @Getter
    @Builder
    public static class AlarmDetailDTO {
        private Long alarmId;
        private String pushToken;

        private Long stationId;
        private String stationName;
        private String stationLine;
        private Direction direction;

        private LocalTime referenceTime;
        private AlarmPeriod alarmPeriod;
        private AlarmDay alarmDay;
        private LocalTime alarmTime;
    }
}
