package com.tave.weathertago.service.alarm;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.tave.weathertago.apiPayload.code.status.ErrorStatus;
import com.tave.weathertago.apiPayload.exception.handler.AlarmHandler;
import com.tave.weathertago.converter.AlarmConverter;
import com.tave.weathertago.domain.Alarm;
import com.tave.weathertago.domain.AlarmDay;
import com.tave.weathertago.domain.AlarmPeriod;
import com.tave.weathertago.dto.alarm.AlarmFcmMessageDto;
import com.tave.weathertago.repository.AlarmRepository;
import com.tave.weathertago.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlarmSendServiceImpl implements AlarmSendService {

    private final AlarmRepository alarmRepository;
    private final UserRepository userRepository;
    private final FirebaseMessaging firebaseMessaging;
    private final RedisTemplate<String, String> redisTemplate;

    private static final String REDIS_KEY_PREFIX = "pushtoken:";

    @Override
    public AlarmFcmMessageDto sendAlarm(Long alarmId){
        // 1. 알람 정보 조회
        Alarm alarm = alarmRepository.findById(alarmId)
                .orElseThrow(() -> new AlarmHandler(ErrorStatus.ALARM_NOT_FOUND));

        // 2. FCM 메시지 생성
        // alarmDay에 따라 알림 대상(오늘/내일) 결정
        String alarmDayStr="";
        LocalDate weatherDate;

        String refTimeStr = String.valueOf(alarm.getReferenceTime()); // 예: "14:30"
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
            default -> {
                throw new AlarmHandler(ErrorStatus.ALARM_INVALID_INPUT);
            }
        }

        // 4. Redis에서 해당 해시에서 datetime이 일치하는지 체크


        // 혼잡도/날씨 mock 데이터
        // String congestionMock = "여유"; // 예: "여유", "보통", "혼잡"
        int congestionMock = 62; // 혼잡도 퍼센트(예: 62%)
        String weatherMock = "맑음, 25°C"; // 예: "맑음, 25°C"

        // 제목
        String title = "[Weathertago] " + alarm.getStationId().getName() + " "  + alarm.getStationId().getLine() + " "  + alarm.getStationId().getDirection()+ " " + alarm.getAlarmTime() + " 혼잡도 알림";

        // 본문
        String body = String.format(
                "%s %s 기준 혼잡도 및 날씨 정보입니다.\n" +
                        "혼잡도: %d%%\\n" +
                        "날씨: %s",
                alarmDayStr,
                alarm.getReferenceTime(),
                congestionMock,
                weatherMock
        );

        log.info(title);
        log.info(body);


        String userId = String.valueOf(alarm.getUserId().getId());
        String redisKey = REDIS_KEY_PREFIX + userId;
        Set<String> pushTokens = redisTemplate.opsForSet().members(redisKey);

        if (pushTokens == null || pushTokens.isEmpty()) {
            log.warn("푸시 토큰이 없습니다: userId={}", userId);
            throw new AlarmHandler(ErrorStatus.ALARM_SEND_FAIL);
        }

        // 4. 각 PushToken에 대해 FCM 메시지 빌드 및 발송
        boolean sent = false;
        for (String token : pushTokens) {
            try {
                Message message = Message.builder()
                        .setToken(token)
                        .setNotification(Notification.builder()
                                .setTitle(title)
                                .setBody(body)
                                .build())
                        .build();

                String response = firebaseMessaging.send(message);
                log.info("FCM 메시지 전송 성공: token={}, response={}", token, response);
                sent = true;
            } catch (Exception e) {
                log.error("FCM 메시지 전송 실패: token={}, error={}", token, e.getMessage());
                // 필요시, 실패한 토큰을 Redis에서 삭제하는 로직 추가 가능
            }
        }

        if (!sent) {
            throw new AlarmHandler(ErrorStatus.ALARM_SEND_FAIL);
        }
        // 5. 전송 결과 DTO 반환
        return AlarmConverter.toAlarmFcmMessageDto(title, body);

    }

    // 자동 알림 전송 스케줄러 (1분마다 실행)
//    @Scheduled(cron = "0 * * * * *") // 매 분 0초마다
//    public void autoSendAlarms() {
//        LocalTime now = LocalTime.now().withSecond(0).withNano(0); // 현재 시각 (초, 나노초 0으로)
//        DayOfWeek today = LocalDate.now().getDayOfWeek(); // 오늘 요일
//
//        // 1. 매일 알림
//        List<Alarm> everydayTodayAlarms = alarmRepository.findAllByAlarmPeriodAndAlarmDayAndAlarmTime(
//                AlarmPeriod.EVERYDAY, AlarmDay.TODAY, now);
//        List<Alarm> everydayYesterdayAlarms = alarmRepository.findAllByAlarmPeriodAndAlarmDayAndAlarmTime(
//                AlarmPeriod.EVERYDAY, AlarmDay.YESTERDAY, now);
//
//        // 2. 요일별 알림 (월~일)
//        AlarmPeriod todayPeriod = switch (today) {
//            case MONDAY -> AlarmPeriod.MONDAY;
//            case TUESDAY -> AlarmPeriod.TUESDAY;
//            case WEDNESDAY -> AlarmPeriod.WEDNESDAY;
//            case THURSDAY -> AlarmPeriod.THURSDAY;
//            case FRIDAY -> AlarmPeriod.FRIDAY;
//            case SATURDAY -> AlarmPeriod.SATURDAY;
//            case SUNDAY -> AlarmPeriod.SUNDAY;
//        };
//
//        List<Alarm> weekdayTodayAlarms = alarmRepository.findAllByAlarmPeriodAndAlarmDayAndAlarmTime(
//                todayPeriod, AlarmDay.TODAY, now);
//        List<Alarm> weekdayYesterdayAlarms = alarmRepository.findAllByAlarmPeriodAndAlarmDayAndAlarmTime(
//                todayPeriod, AlarmDay.YESTERDAY, now);
//
//
//
//        // 알람 전송 (매일)
//        everydayTodayAlarms.forEach(alarm -> {
//            try { sendAlarm(alarm.getAlarmId()); }
//            catch (Exception e) { log.error("매일-당일 알람 전송 실패: {}", e.getMessage()); }
//        });
//        everydayYesterdayAlarms.forEach(alarm -> {
//            try { sendAlarm(alarm.getAlarmId()); }
//            catch (Exception e) { log.error("매일-전날 알람 전송 실패: {}", e.getMessage()); }
//        });
//
//        // 알람 전송 (요일별)
//        weekdayTodayAlarms.forEach(alarm -> {
//            try { sendAlarm(alarm.getAlarmId()); }
//            catch (Exception e) { log.error("요일별-당일 알람 전송 실패: {}", e.getMessage()); }
//        });
//        weekdayYesterdayAlarms.forEach(alarm -> {
//            try { sendAlarm(alarm.getAlarmId()); }
//            catch (Exception e) { log.error("요일별-전날 알람 전송 실패: {}", e.getMessage()); }
//        });
//    }
}
