package com.tave.weathertago.domain;

import com.tave.weathertago.domain.common.BaseEntity;
import com.tave.weathertago.domain.enums.AlarmDay;
import com.tave.weathertago.domain.enums.AlarmPeriod;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalTime;

@Entity
@Table(name="alarm")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Alarm extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long alarmId;

    // user 테이블에서 가져오기
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User userId;

    // 매일, 월화수목금토일
    @Enumerated(EnumType.STRING) @Column(nullable = false)
    private AlarmPeriod alarmPeriod;

    // 혼잡도 계산할 시간 (HH:mm 형식)
    // alarm.setReferenceTime(LocalTime.of(8, 30)); // 오전 8시 30분
    // MySQL: 08:30
    @Column(nullable = false)
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime referenceTime;

    // station 테이블에서 가져온 station_id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id", nullable = false)
    private Station stationId;

    // 알림 보내는 시점: 당일, 하루 전
    @Enumerated(EnumType.STRING) // Enum 값을 문자열로 저장
    @Column(nullable = false)
    private AlarmDay alarmDay;

    // 알림 보내는 시간 (HH:mm 형식)
    @Column(nullable = false)
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime alarmTime;

}