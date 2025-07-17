package com.tave.weathertago.dto.alarm;

import com.tave.weathertago.domain.enums.AlarmDay;
import com.tave.weathertago.domain.enums.AlarmPeriod;
import lombok.Builder;
import lombok.Getter;

public class AlarmRequestDTO {

    @Getter
    @Builder
    public static class AlarmCreateRequestDTO {
        private String stationName;
        private String stationLine;
        private String direction;

        private String referenceTime;
        private AlarmPeriod alarmPeriod;
        private AlarmDay alarmDay;
        private String alarmTime;
    }

    @Getter
    @Builder
    public static class AlarmUpdateRequestDTO {
        private Long alarmId;

        private String stationName;
        private String stationLine;
        private String direction;

        private String referenceTime;
        private AlarmPeriod alarmPeriod;
        private AlarmDay alarmDay;
        private String alarmTime;
    }
}
