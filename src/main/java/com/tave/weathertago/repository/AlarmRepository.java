package com.tave.weathertago.repository;

import com.tave.weathertago.domain.Alarm;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {
    List<Alarm> findByUserId(Long userId);

    Optional<Alarm> findByAlarmId(Long alarmId);
}
