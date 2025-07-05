package com.tave.weathertago.service.alarm;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.tave.weathertago.converter.AlarmConverter;
import com.tave.weathertago.domain.Alarm;
import com.tave.weathertago.domain.AlarmDay;
import com.tave.weathertago.dto.alarm.AlarmFcmMessageDto;
import com.tave.weathertago.repository.AlarmRepository;
import com.tave.weathertago.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlarmSendServiceImpl implements AlarmSendService {

    private final AlarmRepository alarmRepository;
    private final UserRepository userRepository;
    private final FirebaseMessaging firebaseMessaging;
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public AlarmFcmMessageDto sendAlarm(Long alarmId){
        // 1. 알람 정보 조회
        Alarm alarm = alarmRepository.findById(alarmId)
                .orElseThrow(() -> new RuntimeException("Alarm not found"));

        // 2. FCM 메시지 생성
        // alarmDay에 따라 알림 대상(오늘/내일) 결정
        String alarmDayStr="";
        LocalDate weatherDate;

        String refTimeStr = String.valueOf(alarm.getReferenceTime()); // 예: "14:30:00"
        LocalTime localTime = LocalTime.parse(refTimeStr, DateTimeFormatter.ofPattern("HH:mm"));
        LocalDateTime refDateTime = LocalDate.now().atTime(localTime);

        switch (alarm.getAlarmDay()) {
            case TODAY -> {
                alarmDayStr = "오늘";
                weatherDate = refDateTime.toLocalDate();
            }
            case YESTERDAY -> {
                alarmDayStr = "내일";
                weatherDate = refDateTime.toLocalDate().plusDays(1);
            }
        }

        // 4. Redis에서 해당 해시에서 datetime이 일치하는지 체크


        String weatherInfo="";

        // 혼잡도/날씨 mock 데이터
        // String congestionMock = "여유"; // 예: "여유", "보통", "혼잡"
        int congestionMock = 62; // 혼잡도 퍼센트(예: 62%)
        String weatherMock = "맑음, 25°C"; // 예: "맑음, 25°C"

        // 제목
        String title = "[Weathertago] " + alarm.getStationId().getName() + " "  + alarm.getStationId().getLine() + " "  + alarm.getDirection()+ " " + alarm.getAlarmTime() + " 혼잡도 알림";

        // 본문
        String body = String.format(
                "%s %s 기준 혼잡도 및 날씨 정보입니다.\n" +
                        "혼잡도: %d%%\\n" +
                        "날씨: %s",
                alarmDayStr,
                alarm.getReferenceTime(),
                congestionMock,
                weatherInfo
        );

        // 3. FCM 메시지 빌드
        Message message = Message.builder()
                .setToken(alarm.getPushToken())
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .build();


        try {
            // 4. FCM 메시지 전송
            String response = firebaseMessaging.send(message);
            log.info("FCM 메시지 전송 성공: {}", response);

            // 5. 전송 결과 DTO 반환
            return AlarmConverter.toAlarmFcmMessageDto(alarm, title, body);

        } catch (Exception e) {
            log.error("FCM 메시지 전송 실패: {}", e.getMessage());
            throw new RuntimeException("FCM 메시지 전송 실패", e);
        }

    }

    // 자동 알림 전송 스케줄러 (1분마다 실행)
//    @Scheduled(cron = "0 * * * * *") // 매 분 0초마다
//    public void autoSendAlarms() {
//        LocalTime now = LocalTime.now().withSecond(0).withNano(0); // 현재 시각 (초, 나노초 0으로)
//        // 오늘/내일 기준 알람 모두 조회
//        List<Alarm> todayAlarms = alarmRepository.findAllByAlarmDayAndAlarmTime(AlarmDay.TODAY, now);
//        List<Alarm> yesterdayAlarms = alarmRepository.findAllByAlarmDayAndAlarmTime(AlarmDay.YESTERDAY, now);
//
//        // referenceTime 기준 오늘 알람 전송
//        todayAlarms.forEach(alarm -> {
//            try {
//                sendAlarm(alarm.getAlarmId());
//            } catch (Exception e) {
//                log.error("오늘 알람 전송 실패: {}", e.getMessage());
//            }
//        });
//
//        // referenceTime 기준 전날 알람 전송
//        yesterdayAlarms.forEach(alarm -> {
//            try {
//                sendAlarm(alarm.getAlarmId());
//            } catch (Exception e) {
//                log.error("전날 알람 전송 실패: {}", e.getMessage());
//            }
//        });
//    }
}
