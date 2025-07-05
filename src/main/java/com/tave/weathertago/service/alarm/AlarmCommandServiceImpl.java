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
    private final StationRepository stationRepository;

    @Override
    public Optional<AlarmResponseDTO.AlarmDetailDTO> createAlarm(AlarmRequestDTO.AlarmCreateRequestDTO dto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String kakaoId =  authentication.getName();

        User user = userRepository.findByKakaoId(kakaoId)
                .orElseThrow(()->new UserHandler(ErrorStatus.USER_NOT_FOUND));

        Station station = stationRepository.getReferenceById(dto.getStationId());

        Alarm alarm = Alarm.builder()
                .userId(user)
                .pushToken(dto.getPushToken())
                .referenceTime(dto.getReferenceTime())
                .stationId(station)
                .direction(dto.getDirection())
                .alarmDay(dto.getAlarmDay())
                .alarmTime(dto.getAlarmTime())
                .alarmPeriod(dto.getAlarmPeriod())
                .build();

        Alarm savedAlarm = alarmRepository.save(alarm);

        return Optional.of(AlarmConverter.toAlarmDetailDTO(savedAlarm));
    }


    @Override
    public void updateAlarm(AlarmRequestDTO.AlarmUpdateRequestDTO dto){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String kakaoId =  authentication.getName();

        User user = userRepository.findByKakaoId(kakaoId)
                .orElseThrow(()->new UserHandler(ErrorStatus.USER_NOT_FOUND));

        Alarm alarm = alarmRepository.findById(dto.getAlarmId())
                .orElseThrow(() -> new RuntimeException("Alarm not found"));

        Station station = stationRepository.getReferenceById(dto.getStationId());

        if (dto.getPushToken() != null) {
            alarm.setPushToken(dto.getPushToken());
        }
        if (dto.getReferenceTime() != null) {
            alarm.setReferenceTime(dto.getReferenceTime());
        }
        if (dto.getStationId() != null) {
            alarm.setStationId(station);
        }
        if (dto.getDirection() != null) {
            alarm.setDirection(dto.getDirection());
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
    }

    @Override
    public void deleteAlarm(Long alarmId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String kakaoId =  authentication.getName();

        User user = userRepository.findByKakaoId(kakaoId)
                .orElseThrow(()->new UserHandler(ErrorStatus.USER_NOT_FOUND));

        Alarm alarm = alarmRepository.findById(alarmId)
                .orElseThrow(() -> new RuntimeException("Alarm not found"));

        alarmRepository.delete(alarm);
    }

}

