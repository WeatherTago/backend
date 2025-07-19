package com.tave.weathertago.repository;

import com.tave.weathertago.domain.*;
import com.tave.weathertago.domain.enums.AlarmDay;
import com.tave.weathertago.domain.enums.AlarmPeriod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {
    List<Alarm> findByUserId(User user);

    Optional<Alarm> findByAlarmId(Long alarmId);

    @Query("SELECT a FROM Alarm a JOIN FETCH a.stationId JOIN FETCH a.userId " +
            "WHERE a.alarmPeriod = :alarmPeriod AND a.alarmDay = :alarmDay AND a.alarmTime = :alarmTime")
    List<Alarm> findAllWithStationAndUserByAlarmPeriodAndAlarmDayAndAlarmTime(
            @Param("alarmPeriod") AlarmPeriod alarmPeriod,
            @Param("alarmDay") AlarmDay alarmDay,
            @Param("alarmTime") LocalTime alarmTime);


    @Query("SELECT a FROM Alarm a JOIN FETCH a.stationId JOIN FETCH a.userId WHERE a.alarmId = :alarmId")
    Optional<Alarm> findByIdWithStationAndUser(@Param("alarmId") Long alarmId);


}
