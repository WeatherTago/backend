package com.tave.weathertago.service.alarm;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.tave.weathertago.apiPayload.code.status.ErrorStatus;
import com.tave.weathertago.apiPayload.exception.handler.AlarmHandler;
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
    private final StationRepository stationRepository;

    @Override
    public Optional<AlarmResponseDTO.AlarmDetailDTO> createAlarm(AlarmRequestDTO.AlarmCreateRequestDTO dto) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String kakaoId = authentication.getName();

            User user = userRepository.findByKakaoId(kakaoId)
                    .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));

            Station station = stationRepository.getReferenceById(dto.getStationId());

            Alarm alarm = Alarm.builder()
                    .userId(user)
                    .pushToken(dto.getPushToken())
                    .referenceTime(dto.getReferenceTime())
                    .stationId(station)
                    .alarmDay(dto.getAlarmDay())
                    .alarmTime(dto.getAlarmTime())
                    .alarmPeriod(dto.getAlarmPeriod())
                    .build();

            Alarm savedAlarm = alarmRepository.save(alarm);

            return Optional.of(AlarmConverter.toAlarmDetailDTO(savedAlarm));
        } catch (Exception e) {
            throw new AlarmHandler(ErrorStatus.ALARM_CREATE_FAIL);
        }
    }



    @Override
    public void updateAlarm(AlarmRequestDTO.AlarmUpdateRequestDTO dto){
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String kakaoId = authentication.getName();

            User user = userRepository.findByKakaoId(kakaoId)
                    .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));

            Alarm alarm = alarmRepository.findById(dto.getAlarmId())
                    .orElseThrow(() -> new AlarmHandler(ErrorStatus.ALARM_NOT_FOUND));

            // 권한 체크 (본인 알림만 수정 가능)
            if (!alarm.getUserId().getId().equals(user.getId())) {
                throw new AlarmHandler(ErrorStatus.ALARM_FORBIDDEN);
            }

            if (dto.getPushToken() != null) {
                alarm.setPushToken(dto.getPushToken());
            }
            if (dto.getReferenceTime() != null) {
                alarm.setReferenceTime(dto.getReferenceTime());
            }
            if (dto.getStationId() != null) {
                Station station = stationRepository.getReferenceById(dto.getStationId());
                alarm.setStationId(station);
            }
            if (dto.getAlarmDay() != null) {
                alarm.setAlarmDay(dto.getAlarmDay());
            }
            if (dto.getAlarmTime() != null) {
                alarm.setAlarmTime(dto.getAlarmTime());
            }
            if (dto.getAlarmPeriod() != null) {
                alarm.setAlarmPeriod(dto.getAlarmPeriod());
            }
            alarmRepository.save(alarm);
        } catch (Exception e) {
            throw new AlarmHandler(ErrorStatus.ALARM_UPDATE_FAIL);
        }
    }

    @Override
    public void deleteAlarm(Long alarmId){
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String kakaoId = authentication.getName();

            User user = userRepository.findByKakaoId(kakaoId)
                    .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));

            Alarm alarm = alarmRepository.findById(alarmId)
                    .orElseThrow(() -> new AlarmHandler(ErrorStatus.ALARM_NOT_FOUND));

            // 권한 체크 (본인 알림만 삭제 가능)
            if (!alarm.getUserId().getId().equals(user.getId())) {
                throw new AlarmHandler(ErrorStatus.ALARM_FORBIDDEN);
            }

            alarmRepository.delete(alarm);
        } catch (Exception e) {
            throw new AlarmHandler(ErrorStatus.ALARM_DELETE_FAIL);
        }
    }




}

