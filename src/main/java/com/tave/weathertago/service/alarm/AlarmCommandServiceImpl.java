package com.tave.weathertago.service.alarm;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.tave.weathertago.apiPayload.code.status.ErrorStatus;
import com.tave.weathertago.apiPayload.exception.handler.UserHandler;
import com.tave.weathertago.converter.AlarmConverter;
import com.tave.weathertago.domain.Alarm;
import com.tave.weathertago.domain.Station;
import com.tave.weathertago.domain.User;
import com.tave.weathertago.dto.alarm.AlarmFcmMessageDto;
import com.tave.weathertago.dto.alarm.AlarmRequestDTO;
import com.tave.weathertago.dto.alarm.AlarmResponseDTO;
import com.tave.weathertago.repository.AlarmRepository;
import com.tave.weathertago.repository.StationRepository;
import com.tave.weathertago.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AlarmCommandServiceImpl implements AlarmCommandService {

    private final AlarmRepository alarmRepository;
    private final UserRepository userRepository;

    @Override
    public Optional<AlarmResponseDTO.AlarmDetailDTO> createAlarm(AlarmRequestDTO.AlarmCreateRequestDTO dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String kakaoId =  authentication.getName();

        User user = userRepository.findByKakaoId(kakaoId)
                .orElseThrow(()->new UserHandler(ErrorStatus.USER_NOT_FOUND));

        Alarm alarm = Alarm.builder()
                .userId(user)
                .pushToken(dto.getPushToken())
                .referenceTime(dto.getReferenceTime())
                .stationName(dto.getStationName())
                .alarmDay(dto.getAlarmDay())
                .alarmTime(dto.getAlarmTime())
                .build();

        Alarm savedAlarm = alarmRepository.save(alarm);

        return Optional.of(AlarmConverter.toAlarmDetailDTO(savedAlarm));
    }


    @Override
    public void updateAlarm(AlarmRequestDTO.AlarmUpdateRequestDTO dto){
        Alarm alarm = alarmRepository.findById(dto.getAlarmId())
                .orElseThrow(() -> new RuntimeException("Alarm not found"));

        if (dto.getPushToken() != null) {
            alarm.setPushToken(dto.getPushToken());
        }
        if (dto.getReferenceTime() != null) {
            alarm.setReferenceTime(dto.getReferenceTime());
        }
        if (dto.getStationName() != null) {
            alarm.setStationName(dto.getStationName());
        }
        if (dto.getAlarmDay() != null) {
            alarm.setAlarmDay(dto.getAlarmDay());
        }
        if (dto.getAlarmTime() != null) {
            alarm.setAlarmTime(dto.getAlarmTime());
        }
        alarmRepository.save(alarm);
    }

    @Override
    public void deleteAlarm(Long alarmId){

        Alarm alarm = alarmRepository.findById(alarmId)
                .orElseThrow(() -> new RuntimeException("Alarm not found"));

        alarmRepository.delete(alarm);
    }

    @Override
    public AlarmFcmMessageDto sendAlarm(Long alarmId){
        // 1. 알람 정보 조회
        Alarm alarm = alarmRepository.findById(alarmId)
                .orElseThrow(() -> new RuntimeException("Alarm not found"));

        // 2. 알림 제목/본문 설정 (필요에 따라 커스터마이즈)
        String title = "지하철 알림";
        String body = String.format("%s역, %s에 알람이 설정되었습니다.",
                alarm.getStationName(),
                alarm.getAlarmTime().toString());

        // 3. FCM 메시지 생성
        Message message = Message.builder()
                .setToken(alarm.getPushToken())
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .build();

        // 4. FCM 메시지 전송
        try {
            String response = FirebaseMessaging.getInstance().send(message);
            System.out.println("Successfully sent message: " + response);
        } catch (Exception e) {
            e.printStackTrace();
            // 필요하다면 예외 처리 로직 추가
        }

        // 5. 전송에 사용한 정보를 DTO로 만들어 반환
        return AlarmFcmMessageDto.builder()
                .pushToken(alarm.getPushToken())
                .title(title)
                .body(body)
                .build();

    }

    }

