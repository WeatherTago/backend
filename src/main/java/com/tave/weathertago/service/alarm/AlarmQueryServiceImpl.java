package com.tave.weathertago.service.alarm;

import com.google.j2objc.annotations.ObjectiveCName;
import com.tave.weathertago.apiPayload.code.status.ErrorStatus;
import com.tave.weathertago.apiPayload.exception.handler.UserHandler;
import com.tave.weathertago.converter.AlarmConverter;
import com.tave.weathertago.domain.Alarm;
import com.tave.weathertago.domain.User;
import com.tave.weathertago.dto.alarm.AlarmResponseDTO;
import com.tave.weathertago.repository.AlarmRepository;
import com.tave.weathertago.repository.StationRepository;
import com.tave.weathertago.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlarmQueryServiceImpl implements AlarmQueryService {
    
    private final AlarmRepository alarmRepository;
    private final UserRepository userRepository;
    
    @Override
    public Optional<AlarmResponseDTO.AlarmDetailDTO> getAlarmDetail(Long alarmId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String kakaoId =  authentication.getName();

        User user = userRepository.findByKakaoId(kakaoId)
                .orElseThrow(()->new UserHandler(ErrorStatus.USER_NOT_FOUND));

        return alarmRepository.findByAlarmId(alarmId)
                .map(AlarmConverter::toAlarmDetailDTO);
    }

    @Override
    public List<AlarmResponseDTO.AlarmDetailDTO> getAlarms() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String kakaoId =  authentication.getName();

        User user = userRepository.findByKakaoId(kakaoId)
                .orElseThrow(()->new UserHandler(ErrorStatus.USER_NOT_FOUND));

        List<Alarm> alarms = alarmRepository.findByUserId(user);
        return alarms.stream()
                .map(AlarmConverter::toAlarmDetailDTO)
                .collect(Collectors.toList());
    }

}
