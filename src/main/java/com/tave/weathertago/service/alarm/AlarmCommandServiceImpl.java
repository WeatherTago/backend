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
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

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

            // 역 이름 검증
            if (!stationRepository.existsByName(dto.getStationName())) {
                throw new AlarmHandler(ErrorStatus.STATION_NAME_NOT_FOUND);
            }
            // 호선 검증
            if (!stationRepository.existsByNameAndLine(dto.getStationName(), dto.getStationLine())) {
                throw new AlarmHandler(ErrorStatus.STATION_LINE_NOT_FOUND);
            }
            // 방향 검증
            if (!stationRepository.existsByNameAndLineAndDirection(dto.getStationName(), dto.getStationLine(), dto.getDirection())) {
                throw new AlarmHandler(ErrorStatus.INVALID_DIRECTION);
            }

            Station station = stationRepository.findByNameAndLineAndDirection(
                    dto.getStationName(),
                    dto.getStationLine(),
                    dto.getDirection()
            ).orElseThrow(() -> new AlarmHandler(ErrorStatus.STATION_ID_NOT_FOUND));

            Alarm alarm = Alarm.builder()
                    .userId(user)
                    .referenceTime(parseLocalTime(dto.getReferenceTime()))
                    .stationId(station)
                    .alarmDay(dto.getAlarmDay())
                    .alarmTime(parseLocalTime(dto.getAlarmTime()))
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
            if (dto.getReferenceTime() != null) {
                alarm.setReferenceTime(parseLocalTime(dto.getReferenceTime()));
            }
            // updateAlarm
            if (dto.getStationName() != null && dto.getStationLine() != null && dto.getDirection() != null) {
                if (!stationRepository.existsByName(dto.getStationName())) {
                    throw new AlarmHandler(ErrorStatus.STATION_NAME_NOT_FOUND);
                }
                if (!stationRepository.existsByNameAndLine(dto.getStationName(), dto.getStationLine())) {
                    throw new AlarmHandler(ErrorStatus.STATION_LINE_NOT_FOUND);
                }
                if (!stationRepository.existsByNameAndLineAndDirection(dto.getStationName(), dto.getStationLine(), dto.getDirection())) {
                    throw new AlarmHandler(ErrorStatus.INVALID_DIRECTION);
                }


                Station station = stationRepository.findByNameAndLineAndDirection(
                        dto.getStationName(),
                        dto.getStationLine(),
                        dto.getDirection()
                ).orElseThrow(() -> new AlarmHandler(ErrorStatus.STATION_ID_NOT_FOUND));
                alarm.setStationId(station);
            }
            if (dto.getAlarmDay() != null) {
                alarm.setAlarmDay(dto.getAlarmDay());
            }
            if (dto.getAlarmTime() != null) {
                alarm.setAlarmTime(parseLocalTime(dto.getAlarmTime()));
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

    private static LocalTime parseLocalTime(String timeStr) {
        if (timeStr == null) return null;
        return LocalTime.parse(timeStr, DateTimeFormatter.ofPattern("HH:mm"));
    }


}

