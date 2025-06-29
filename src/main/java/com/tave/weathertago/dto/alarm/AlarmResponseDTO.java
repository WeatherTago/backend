package com.tave.weathertago.dto.alarm;

import com.tave.weathertago.domain.AlarmDay;
import com.tave.weathertago.domain.Station;
import com.tave.weathertago.dto.CongestionDTO;
import com.tave.weathertago.dto.WeatherDTO;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;
import java.time.LocalTime;

public class AlarmResponseDTO {

    @Getter
    @Builder
    public static class AlarmDetailDTO {
        private Long userId;
        private Long alarmId;
        private String pushToken;
        private LocalTime referenceTime;
        private Station stationName;
        private AlarmDay alarmDay;
        private LocalTime alarmTime;

        private WeatherDTO weather;
        private CongestionDTO congestion;

    }
}
