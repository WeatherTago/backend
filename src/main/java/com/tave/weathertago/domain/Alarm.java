package com.tave.weathertago.domain;

import com.tave.weathertago.domain.common.BaseEntity;
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

    private String pushToken;

    // 혼잡도 계산할 시간 (HH:mm 형식)
    // alarm.setReferenceTime(LocalTime.of(8, 30)); // 오전 8시 30분
    // MySQL: 08:30:00
    @Column(nullable = false)
    @DateTimeFormat(pattern = "HH:mm:ss")
    private LocalTime referenceTime;

    // 역 이름 (즐겨찾기 테이블에 있는 역 불러오기)
    @Column(nullable = false)
    private String stationName;

    // 알람 보내는 시점: 당일, 하루 전
    @Enumerated(EnumType.STRING) // Enum 값을 문자열로 저장
    @Column(nullable = false)
    private AlarmDay alarmDay;

    // 알람 보내는 시간 (HH:mm 형식)
    @Column(nullable = false)
    @DateTimeFormat(pattern = "HH:mm:ss")
    private LocalTime alarmTime;

}