package com.tave.weathertago.service.alarm;

import com.tave.weathertago.apiPayload.code.status.ErrorStatus;
import com.tave.weathertago.apiPayload.exception.handler.AlarmHandler;
import com.tave.weathertago.converter.AlarmConverter;
import com.tave.weathertago.domain.Alarm;
import com.tave.weathertago.domain.enums.AlarmDay;
import com.tave.weathertago.domain.enums.AlarmPeriod;
import com.tave.weathertago.dto.alarm.AlarmFcmMessageDto;
import com.tave.weathertago.dto.prediction.PredictionResponseDTO;
import com.tave.weathertago.dto.prediction.PredictionWithWeatherResponseDTO;
import com.tave.weathertago.dto.weather.WeatherResponseDTO;
import com.tave.weathertago.repository.AlarmRepository;
import com.tave.weathertago.service.congestion.CongestionQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlarmSendServiceImpl implements AlarmSendService {

    private final AlarmRepository alarmRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final CongestionQueryService congestionQueryService;
    private final RestTemplate restTemplate;

    private static final String REDIS_KEY_PREFIX = "pushtoken:";
    private static final String EXPO_PUSH_URL = "https://exp.host/--/api/v2/push/send";
    private static final String EXPO_RECEIPT_URL = "https://exp.host/--/api/v2/push/getReceipts";
    private static final int BATCH_SIZE = 100; // 한 번에 전송할 최대 알림 수 (Expo 제한)
    private static final int RECEIPT_BATCH_SIZE = 1000; // 한 번에 확인할 최대 영수증 수

    @Override
    @Transactional
    public AlarmFcmMessageDto sendAlarm(Long alarmId) {
        // 알림 설정 정보 조회
        Alarm alarm = getAlarmById(alarmId);

        // 알림 기준 시간 계산 (오늘/어제 기준)
        AlarmTimeInfo timeInfo = calculateAlarmTime(alarm);
        PredictionWithWeatherResponseDTO result = getCongestionAndWeather(alarm, timeInfo.refDateTime());

        String title = createNotificationTitle(alarm);
        String body = createNotificationBody(alarm, timeInfo, result);

        Set<String> expoPushTokens = getExpoPushTokens(alarm.getUserId().getId());
        List<String> receiptIds = sendExpoPushNotifications(expoPushTokens, title, body);

        validateAlarmSent(alarmId, receiptIds);

        log.info("알림 전송 완료: alarmId={}, receiptCount={}", alarmId, receiptIds.size());
        return AlarmConverter.toAlarmFcmMessageDto(title, body);
    }

    @Scheduled(cron = "0 * * * * *") // 매 분 0초마다 실행
    public void autoSendAlarms() {
        LocalTime now = LocalTime.now().withSecond(0).withNano(0);
        DayOfWeek today = LocalDate.now().getDayOfWeek();

        log.info("자동 알림 전송 시작: {}", now);

        try {
            // 매일 알림 전송
            sendDailyAlarms(now);

            // 요일별 알림 전송
            sendWeeklyAlarms(now, today);

        } catch (Exception e) {
            log.error("자동 알림 전송 중 오류 발생: {}", e.getMessage(), e);
        }
    }

    // ===== Private Methods =====

    private record AlarmTimeInfo(String dayStr, LocalDateTime refDateTime) {}

    private Alarm getAlarmById(Long alarmId) {
        return alarmRepository.findById(alarmId)
                .orElseThrow(() -> new AlarmHandler(ErrorStatus.ALARM_NOT_FOUND));
    }

    private AlarmTimeInfo calculateAlarmTime(Alarm alarm) {
        LocalTime localTime = alarm.getReferenceTime();

        return switch (alarm.getAlarmDay()) {
            case TODAY -> new AlarmTimeInfo(
                    "오늘",
                    LocalDate.now().atTime(localTime).withSecond(0)
            );
            case YESTERDAY -> new AlarmTimeInfo(
                    "내일",
                    LocalDate.now().plusDays(1).atTime(localTime).withSecond(0)
            );
            default -> throw new AlarmHandler(ErrorStatus.ALARM_INVALID_INPUT);
        };
    }

    private PredictionWithWeatherResponseDTO getCongestionAndWeather(Alarm alarm, LocalDateTime refDateTime) {
        try {
            // 1. 날짜 유효성 검증
            if (refDateTime == null) {
                log.error("기준 날짜가 null입니다: alarmId={}", alarm.getAlarmId());
                throw new IllegalArgumentException("기준 날짜가 null입니다.");
            }

            // 2. 날짜 정규화 (초와 나노초를 0으로 설정)
            LocalDateTime normalizedDateTime = refDateTime.withSecond(0).withNano(0);

            // 4. 로그 출력 (디버깅용)
            log.debug("혼잡도 및 날씨 정보 조회 시작: alarmId={}, stationId={}, refDateTime={}",
                    alarm.getAlarmId(), alarm.getStationId().getId(), normalizedDateTime);

            // 5. 실제 조회 수행
            PredictionWithWeatherResponseDTO result = congestionQueryService.getCongestionWithWeather(
                    alarm.getStationId().getId(), normalizedDateTime);

            // 6. 결과 검증
            if (result == null) {
                log.error("혼잡도 및 날씨 정보 조회 결과가 null입니다: alarmId={}", alarm.getAlarmId());
                throw new RuntimeException("조회 결과가 null입니다.");
            }

            log.debug("혼잡도 및 날씨 정보 조회 완료: alarmId={}", alarm.getAlarmId());
            return result;

        } catch (IllegalArgumentException e) {
            log.error("날짜 형식 오류: alarmId={}, refDateTime={}, error={}",
                    alarm.getAlarmId(), refDateTime, e.getMessage());
            throw new AlarmHandler(ErrorStatus.ALARM_INVALID_INPUT);

        } catch (DateTimeException e) {
            log.error("날짜 시간 처리 오류: alarmId={}, refDateTime={}, error={}",
                    alarm.getAlarmId(), refDateTime, e.getMessage());
            throw new AlarmHandler(ErrorStatus.ALARM_INVALID_INPUT);

        } catch (Exception e) {
            log.error("혼잡도 및 날씨 정보 조회 실패: alarmId={}, stationId={}, refDateTime={}, error={}",
                    alarm.getAlarmId(),
                    alarm.getStationId() != null ? alarm.getStationId().getId() : null,
                    refDateTime, e.getMessage(), e);
            throw new AlarmHandler(ErrorStatus.ALARM_SEND_FAIL);
        }
    }


    private String createNotificationTitle(Alarm alarm) {
        return String.format("[Weathertago] %s %s %s %s 혼잡도 알림",
                alarm.getStationId().getName(),
                alarm.getStationId().getLine(),
                alarm.getStationId().getDirection(),
                alarm.getAlarmTime());
    }

    private String createNotificationBody(Alarm alarm, AlarmTimeInfo timeInfo,
                                          PredictionWithWeatherResponseDTO result) {
        PredictionResponseDTO prediction = result.getPrediction();
        WeatherResponseDTO weather = result.getWeather();

        String congestionStr = String.format("%s (%.0f%%)",
                prediction.getCongestionLevel(),
                prediction.getCongestionScore());

        String weatherStr = String.format(
                "기온 %.1f°C, 습도 %.1f%%, 강수량 %.1fmm, 풍속 %.1fm/s, 적설 %.1fcm, 풍향 %.1f°",
                weather.getTmp(), weather.getReh(), weather.getPcp(),
                weather.getWsd(), weather.getSno(), weather.getVec());

        return String.format(
                "%s %s 기준 혼잡도 및 날씨 정보입니다.\n혼잡도: %s\n날씨: %s",
                timeInfo.dayStr(),
                alarm.getReferenceTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                congestionStr,
                weatherStr);
    }

    private Set<String> getExpoPushTokens(Long userId) {
        String redisKey = REDIS_KEY_PREFIX + userId;
        Set<String> expoPushTokens = redisTemplate.opsForSet().members(redisKey);

        if (expoPushTokens == null || expoPushTokens.isEmpty()) {
            log.warn("Expo Push Token이 없습니다: userId={}", userId);
            throw new AlarmHandler(ErrorStatus.ALARM_SEND_FAIL);
        }

        return expoPushTokens;
    }

    private List<String> sendExpoPushNotifications(Set<String> expoPushTokens, String title, String body) {
        List<String> receiptIds = new ArrayList<>();
        List<String> tokenList = new ArrayList<>(expoPushTokens);

        for (int i = 0; i < tokenList.size(); i += BATCH_SIZE) {
            List<String> batchTokens = tokenList.subList(i, Math.min(i + BATCH_SIZE, tokenList.size()));

            try {
                List<String> batchReceiptIds = sendBatchNotifications(batchTokens, title, body);
                receiptIds.addAll(batchReceiptIds);
            } catch (Exception e) {
                log.error("배치 알림 전송 실패: batchSize={}, error={}",
                        batchTokens.size(), e.getMessage());
            }
        }

        return receiptIds;
    }

    private List<String> sendBatchNotifications(List<String> tokens, String title, String body) {
        List<String> receiptIds = new ArrayList<>();

        try {
            List<Map<String, Object>> messages = createPushMessages(tokens, title, body);
            HttpEntity<List<Map<String, Object>>> request = createHttpRequest(messages);

            ResponseEntity<Map> response = restTemplate.postForEntity(EXPO_PUSH_URL, request, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                processExpoPushResponse(response.getBody(), receiptIds);
            } else {
                log.error("Expo Push API 호출 실패: status={}", response.getStatusCode());
            }

        } catch (Exception e) {
            log.error("Expo Push 알림 전송 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("푸시 알림 전송 실패", e);
        }

        return receiptIds;
    }

    private List<Map<String, Object>> createPushMessages(List<String> tokens, String title, String body) {
        return tokens.stream()
                .map(token -> {
                    Map<String, Object> message = new HashMap<>();
                    message.put("to", token);
                    message.put("title", title);
                    message.put("body", body);
                    message.put("sound", "default");
                    message.put("priority", "high");
                    return message;
                })
                .toList();
    }

    private HttpEntity<List<Map<String, Object>>> createHttpRequest(List<Map<String, Object>> messages) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return new HttpEntity<>(messages, headers);
    }

    @SuppressWarnings("unchecked")
    private void processExpoPushResponse(Map<String, Object> responseBody, List<String> receiptIds) {
        List<Map<String, Object>> data = (List<Map<String, Object>>) responseBody.get("data");

        if (data != null) {
            data.forEach(ticket -> processTicket(ticket, receiptIds));
        }

        List<Map<String, Object>> errors = (List<Map<String, Object>>) responseBody.get("errors");
        if (errors != null && !errors.isEmpty()) {
            errors.forEach(this::handleRequestError);
        }
    }

    private void processTicket(Map<String, Object> ticket, List<String> receiptIds) {
        String status = (String) ticket.get("status");

        if ("ok".equals(status)) {
            String id = (String) ticket.get("id");
            receiptIds.add(id);
            log.debug("Expo Push 알림 전송 성공: receiptId={}", id);
        } else {
            handleTicketError(ticket);
        }
    }

    @SuppressWarnings("unchecked")
    private void handleTicketError(Map<String, Object> ticket) {
        String message = (String) ticket.get("message");
        Map<String, Object> details = (Map<String, Object>) ticket.get("details");

        log.error("Expo Push 알림 전송 실패: message={}, details={}", message, details);

        if (details != null && "DeviceNotRegistered".equals(details.get("error"))) {
            log.warn("기기가 등록되지 않음: 토큰 제거 필요");
            // TODO: 토큰 제거 로직 구현
        }
    }

    private void handleRequestError(Map<String, Object> error) {
        String code = (String) error.get("code");
        String message = (String) error.get("message");
        log.error("Expo Push 전체 요청 오류: code={}, message={}", code, message);
    }

    private void validateAlarmSent(Long alarmId, List<String> receiptIds) {
        if (receiptIds.isEmpty()) {
            log.warn("알림 전송에 실패했습니다: alarmId={}", alarmId);
            throw new AlarmHandler(ErrorStatus.ALARM_SEND_FAIL);
        }
    }

    // ===== Push Receipt Methods =====

    public void checkPushReceipts(List<String> receiptIds) {
        if (receiptIds == null || receiptIds.isEmpty()) {
            return;
        }

        for (int i = 0; i < receiptIds.size(); i += RECEIPT_BATCH_SIZE) {
            List<String> batchIds = receiptIds.subList(i, Math.min(i + RECEIPT_BATCH_SIZE, receiptIds.size()));

            try {
                checkBatchReceipts(batchIds);
            } catch (Exception e) {
                log.error("영수증 확인 실패: batchSize={}, error={}", batchIds.size(), e.getMessage());
            }
        }
    }

    private void checkBatchReceipts(List<String> receiptIds) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> requestBody = Map.of("ids", receiptIds);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(EXPO_RECEIPT_URL, request, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                processReceiptResponse(response.getBody());
            }

        } catch (Exception e) {
            log.error("푸시 영수증 확인 중 오류 발생: {}", e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    private void processReceiptResponse(Map<String, Object> responseBody) {
        Map<String, Object> data = (Map<String, Object>) responseBody.get("data");

        if (data != null) {
            data.forEach(this::processReceipt);
        }
    }

    @SuppressWarnings("unchecked")
    private void processReceipt(String receiptId, Object receiptObj) {
        Map<String, Object> receipt = (Map<String, Object>) receiptObj;
        String status = (String) receipt.get("status");

        if ("ok".equals(status)) {
            log.debug("알림 전달 성공: receiptId={}", receiptId);
        } else {
            String message = (String) receipt.get("message");
            Map<String, Object> details = (Map<String, Object>) receipt.get("details");
            log.error("알림 전달 실패: receiptId={}, message={}, details={}", receiptId, message, details);

            if (details != null && "DeviceNotRegistered".equals(details.get("error"))) {
                log.warn("기기가 등록되지 않음: 토큰 제거 필요");
                // TODO: 토큰 제거 로직 구현
            }
        }
    }

    // ===== Scheduled Alarm Methods =====

    private void sendDailyAlarms(LocalTime now) {
        List<Alarm> todayAlarms = alarmRepository.findAllWithStationAndUserByAlarmPeriodAndAlarmDayAndAlarmTime(
                AlarmPeriod.EVERYDAY, AlarmDay.TODAY, now);
        List<Alarm> yesterdayAlarms = alarmRepository.findAllWithStationAndUserByAlarmPeriodAndAlarmDayAndAlarmTime(
                AlarmPeriod.EVERYDAY, AlarmDay.YESTERDAY, now);

        sendAlarmBatch(todayAlarms, "매일-오늘");
        sendAlarmBatch(yesterdayAlarms, "매일-내일");
    }

    private void sendWeeklyAlarms(LocalTime now, DayOfWeek today) {
        AlarmPeriod todayPeriod = convertDayOfWeekToAlarmPeriod(today);

        List<Alarm> weeklyTodayAlarms = alarmRepository.findAllWithStationAndUserByAlarmPeriodAndAlarmDayAndAlarmTime(
                todayPeriod, AlarmDay.TODAY, now);
        List<Alarm> weeklyYesterdayAlarms = alarmRepository.findAllWithStationAndUserByAlarmPeriodAndAlarmDayAndAlarmTime(
                todayPeriod, AlarmDay.YESTERDAY, now);

        sendAlarmBatch(weeklyTodayAlarms, "요일별-오늘");
        sendAlarmBatch(weeklyYesterdayAlarms, "요일별-내일");
    }


    private AlarmPeriod convertDayOfWeekToAlarmPeriod(DayOfWeek dayOfWeek) {
        return switch (dayOfWeek) {
            case MONDAY -> AlarmPeriod.MONDAY;
            case TUESDAY -> AlarmPeriod.TUESDAY;
            case WEDNESDAY -> AlarmPeriod.WEDNESDAY;
            case THURSDAY -> AlarmPeriod.THURSDAY;
            case FRIDAY -> AlarmPeriod.FRIDAY;
            case SATURDAY -> AlarmPeriod.SATURDAY;
            case SUNDAY -> AlarmPeriod.SUNDAY;
        };
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    private void sendAlarmBatch(List<Alarm> alarms, String alarmType) {
        if (alarms.isEmpty()) {
            return;
        }

        log.info("{} 알림 전송 시작: {}개", alarmType, alarms.size());

        alarms.forEach(alarm -> {
            try {
                sendAlarm(alarm.getAlarmId());
            } catch (Exception e) {
                log.error("{} 알림 전송 실패: alarmId={}, error={}",
                        alarmType, alarm.getAlarmId(), e.getMessage());
            }
        });
    }
}
