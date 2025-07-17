package com.tave.weathertago.service.alarm;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tave.weathertago.apiPayload.code.status.ErrorStatus;
import com.tave.weathertago.apiPayload.exception.handler.AlarmHandler;
import com.tave.weathertago.converter.AlarmConverter;
import com.tave.weathertago.domain.Alarm;
import com.tave.weathertago.dto.alarm.AlarmFcmMessageDto;
import com.tave.weathertago.dto.prediction.PredictionResponseDTO;
import com.tave.weathertago.dto.prediction.PredictionWithWeatherResponseDTO;
import com.tave.weathertago.dto.weather.WeatherResponseDTO;
import com.tave.weathertago.repository.AlarmRepository;
import com.tave.weathertago.repository.UserRepository;
import com.tave.weathertago.service.congestion.CongestionQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.http.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestTemplate;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlarmSendServiceImpl implements AlarmSendService {

    private final AlarmRepository alarmRepository;
    private final UserRepository userRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final CongestionQueryService congestionQueryService;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;


    private static final String REDIS_KEY_PREFIX = "pushtoken:";
    private static final String EXPO_PUSH_URL = "https://exp.host/--/api/v2/push/send";
    private static final String EXPO_RECEIPT_URL = "https://exp.host/--/api/v2/push/getReceipts";

    @Override
    public AlarmFcmMessageDto sendAlarm(Long alarmId){
        // 1. 알람 정보 조회
        Alarm alarm = alarmRepository.findById(alarmId)
                .orElseThrow(() -> new AlarmHandler(ErrorStatus.ALARM_NOT_FOUND));

        // 2. 알림 날짜 계산
        // alarmDay에 따라 알림 대상(오늘/내일) 결정
        String alarmDayStr = "";
        LocalDateTime refDateTime;
        LocalTime localTime = alarm.getReferenceTime();
        switch (alarm.getAlarmDay()) {
            case TODAY -> {
                alarmDayStr = "오늘";
                refDateTime = LocalDate.now().atTime(localTime).withSecond(0);
            }
            case YESTERDAY -> {
                alarmDayStr = "내일";
                refDateTime = LocalDate.now().plusDays(1).atTime(localTime).withSecond(0);
            }
            default -> throw new AlarmHandler(ErrorStatus.ALARM_INVALID_INPUT);
        }

        log.info(String.valueOf(refDateTime));


        // 3. 혼잡도·날씨 정보 조회
        PredictionWithWeatherResponseDTO result;
        try {
            result = congestionQueryService.getCongestionWithWeather(
                    alarm.getStationId().getId(), refDateTime);
        } catch (Exception e) {
            log.error("혼잡도 및 날씨 정보 조회 실패: alarmId={}, error={}", alarmId, e.getMessage());
            throw new AlarmHandler(ErrorStatus.ALARM_SEND_FAIL);
        }

        PredictionResponseDTO prediction = result.getPrediction();
        WeatherResponseDTO weather = result.getWeather();

        // 4. 메시지 제목 생성
        String title = String.format("[Weathertago] %s %s %s %s 혼잡도 알림",
                alarm.getStationId().getName(),
                alarm.getStationId().getLine(),
                alarm.getStationId().getDirection(),
                alarm.getAlarmTime()
        );

        // 5. 혼잡도·날씨 포맷팅
        String congestionStr = String.format("%s (%.0f%%)",
                prediction.getCongestionLevel(),
                prediction.getCongestionScore() * 100
        );
        String weatherStr = String.format("기온 %.1f°C, 습도 %.1f%%, 강수량 %.1fmm, 풍속 %.1fm/s, 적설 %.1fcm, 풍향 %.1f°",
                weather.getTmp(), weather.getReh(), weather.getPcp(),
                weather.getWsd(), weather.getSno(), weather.getVec()
        );


        // 6. 메시지 본문 생성
        String body = String.format(
                "%s %s 기준 혼잡도 및 날씨 정보입니다.\n" +
                        "혼잡도: %s\n" +
                        "날씨: %s",
                alarmDayStr,
                alarm.getReferenceTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                congestionStr,
                weatherStr
        );

        log.info("알림 제목: {}", title);
        log.info("알림 내용: {}", body);

        // 7. Expo Push Token 조회
        String userId = String.valueOf(alarm.getUserId().getId());
        String redisKey = REDIS_KEY_PREFIX + userId;
        Set<String> expoPushTokens = redisTemplate.opsForSet().members(redisKey);

        if (expoPushTokens == null || expoPushTokens.isEmpty()) {
            log.warn("Expo Push Token이 없습니다: userId={}", userId);
            throw new AlarmHandler(ErrorStatus.ALARM_SEND_FAIL);
        }

        // 8. Expo Push 메시지 전송
        List<String> receiptIds = sendExpoPushNotifications(expoPushTokens, title, body);

        if (receiptIds.isEmpty()) {
            log.warn("알림 전송에 실패했습니다: alarmId={}", alarmId);
            throw new AlarmHandler(ErrorStatus.ALARM_SEND_FAIL);
        }

        log.info("알림 전송 완료: alarmId={}, receiptCount={}", alarmId, receiptIds.size());

        return AlarmConverter.toAlarmFcmMessageDto(title, body);
    }

    private List<String> sendExpoPushNotifications(Set<String> expoPushTokens, String title, String body) {
        List<String> receiptIds = new ArrayList<>();

        // 최대 100개씩 배치로 전송
        List<String> tokenList = new ArrayList<>(expoPushTokens);
        int batchSize = 100;

        for (int i = 0; i < tokenList.size(); i += batchSize) {
            List<String> batchTokens = tokenList.subList(i, Math.min(i + batchSize, tokenList.size()));
            List<String> batchReceiptIds = sendBatchNotifications(batchTokens, title, body);
            receiptIds.addAll(batchReceiptIds);
        }

        return receiptIds;
    }


    private List<String> sendBatchNotifications(List<String> tokens, String title, String body) {
        List<String> receiptIds = new ArrayList<>();

        try {
            // 메시지 배열 생성
            List<Map<String, Object>> messages = new ArrayList<>();

            for (String token : tokens) {
                Map<String, Object> message = new HashMap<>();
                message.put("to", token);
                message.put("title", title);
                message.put("body", body);
                message.put("sound", "default");
                message.put("priority", "high");
                messages.add(message);
            }

            // HTTP 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

            // HTTP 요청 생성
            HttpEntity<List<Map<String, Object>>> request = new HttpEntity<>(messages, headers);

            // Expo Push API 호출
            ResponseEntity<Map> response = restTemplate.postForEntity(EXPO_PUSH_URL, request, Map.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> responseBody = response.getBody();
                List<Map<String, Object>> data = (List<Map<String, Object>>) responseBody.get("data");

                if (data != null) {
                    for (Map<String, Object> ticket : data) {
                        String status = (String) ticket.get("status");
                        if ("ok".equals(status)) {
                            String id = (String) ticket.get("id");
                            receiptIds.add(id);
                            log.info("Expo Push 알림 전송 성공: receiptId={}", id);
                        } else {
                            String message = (String) ticket.get("message");
                            Map<String, Object> details = (Map<String, Object>) ticket.get("details");
                            log.error("Expo Push 알림 전송 실패: message={}, details={}", message, details);

                            // DeviceNotRegistered 오류 처리
                            if (details != null && "DeviceNotRegistered".equals(details.get("error"))) {
                                // 해당 토큰을 Redis에서 제거하는 로직 추가 가능
                                log.warn("기기가 등록되지 않음: 토큰 제거 필요");
                            }
                        }
                    }
                }

                // 전체 요청 오류 확인
                List<Map<String, Object>> errors = (List<Map<String, Object>>) responseBody.get("errors");
                if (errors != null && !errors.isEmpty()) {
                    for (Map<String, Object> error : errors) {
                        String code = (String) error.get("code");
                        String message = (String) error.get("message");
                        log.error("Expo Push 전체 요청 오류: code={}, message={}", code, message);
                    }
                }
            } else {
                log.error("Expo Push API 호출 실패: status={}", response.getStatusCode());
            }

        } catch (Exception e) {
            log.error("Expo Push 알림 전송 중 오류 발생: {}", e.getMessage(), e);
        }

        return receiptIds;
    }

    /**
     * 푸시 영수증 확인 (15분 후에 호출하는 것이 권장됨)
     */
    public void checkPushReceipts(List<String> receiptIds) {
        if (receiptIds == null || receiptIds.isEmpty()) {
            return;
        }

        // 최대 1000개씩 배치로 확인
        int batchSize = 1000;
        for (int i = 0; i < receiptIds.size(); i += batchSize) {
            List<String> batchIds = receiptIds.subList(i, Math.min(i + batchSize, receiptIds.size()));
            checkBatchReceipts(batchIds);
        }
    }

    private void checkBatchReceipts(List<String> receiptIds) {
        try {
            // HTTP 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // 요청 본문 생성
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("ids", receiptIds);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            // 영수증 확인 API 호출
            ResponseEntity<Map> response = restTemplate.postForEntity(EXPO_RECEIPT_URL, request, Map.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> responseBody = response.getBody();
                Map<String, Object> data = (Map<String, Object>) responseBody.get("data");

                if (data != null) {
                    for (Map.Entry<String, Object> entry : data.entrySet()) {
                        String receiptId = entry.getKey();
                        Map<String, Object> receipt = (Map<String, Object>) entry.getValue();
                        String status = (String) receipt.get("status");

                        if ("ok".equals(status)) {
                            log.info("알림 전달 성공: receiptId={}", receiptId);
                        } else {
                            String message = (String) receipt.get("message");
                            Map<String, Object> details = (Map<String, Object>) receipt.get("details");
                            log.error("알림 전달 실패: receiptId={}, message={}, details={}", receiptId, message, details);

                            // DeviceNotRegistered 오류 처리
                            if (details != null && "DeviceNotRegistered".equals(details.get("error"))) {
                                log.warn("기기가 등록되지 않음: 토큰 제거 필요");
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            log.error("푸시 영수증 확인 중 오류 발생: {}", e.getMessage(), e);
        }
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
