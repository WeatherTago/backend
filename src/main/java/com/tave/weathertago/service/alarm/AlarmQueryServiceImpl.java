package com.tave.weathertago.service.alarm;

import com.google.j2objc.annotations.ObjectiveCName;
import com.tave.weathertago.domain.Alarm;
import com.tave.weathertago.dto.alarm.AlarmResponseDTO;
import com.tave.weathertago.repository.AlarmRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlarmQueryServiceImpl implements AlarmQueryService {
    
    private final AlarmRepository alarmRepository;
    
    @Override
    public Optional<AlarmResponseDTO.AlarmDetailDTO> getAlarmDetail(Long alarmId) {
        return alarmRepository.findByAlarmId(alarmId)
                .map(this::toAlarmDetailDTO);
    }

    @Override
    public List<AlarmResponseDTO.AlarmDetailDTO> getAlarmsByUserId(Long userId) {
        List<Alarm> alarms = alarmRepository.findByUserId(userId);
        return alarms.stream()
                .map(this::toAlarmDetailDTO)
                .collect(Collectors.toList());
    }

    // Alarm 엔티티를 AlarmDetailDTO로 변환하는 메서드
    private AlarmResponseDTO.AlarmDetailDTO toAlarmDetailDTO(Alarm alarm) {
        return AlarmResponseDTO.AlarmDetailDTO.builder()
                .userId(alarm.getUserId())
                .alarmId(alarm.getAlarmId())
                .pushToken(alarm.getPushToken())
                .referenceTime(alarm.getReferenceTime())
                .stationName(alarm.getStationName())
                .alarmDay(alarm.getAlarmDay())
                .alarmTime(alarm.getAlarmTime())
                // .weather(weatherDTO) // 필요 시 주입
                // .congestion(congestionDTO) // 필요 시 주입
                .build();
    }
    
}
