package com.tave.weathertago.repository;

import com.tave.weathertago.domain.Alarm;

import java.util.List;
import java.util.Optional;

public interface AlarmRepository {
    List<Alarm> findByUserId(Long userId);

    Alarm findByAlarmId(Long alarmId);
}
