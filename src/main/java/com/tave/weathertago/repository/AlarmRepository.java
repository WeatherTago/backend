package com.tave.weathertago.repository;

import com.tave.weathertago.domain.Alarm;
import com.tave.weathertago.domain.AlarmDay;
import com.tave.weathertago.domain.AlarmPeriod;
import com.tave.weathertago.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {
    List<Alarm> findByUserId(User user);

    Optional<Alarm> findByAlarmId(Long alarmId);

    List<Alarm> findAllByAlarmPeriodAndAlarmDayAndAlarmTime(AlarmPeriod alarmPeriod, AlarmDay alarmDay, LocalTime alarmTime);
}
