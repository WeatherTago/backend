package com.tave.weathertago.controller.alarm;

import com.tave.weathertago.apiPayload.ApiResponse;
import com.tave.weathertago.dto.alarm.AlarmResponseDTO;
import com.tave.weathertago.service.alarm.AlarmQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Alarm", description = "알림 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/me/alarms")
public class AlarmRestController {

    private final AlarmQueryService alarmQueryService;

    @GetMapping("")
    @Operation(summary = "모든 알림 조회", description = "사용자의 모든 알림을 조회합니다.")
    public ResponseEntity<ApiResponse<List<AlarmResponseDTO.AlarmDetailDTO>>> getAlarms() {
        List<AlarmResponseDTO.AlarmDetailDTO> alarms = alarmQueryService.getAlarms();
        return ResponseEntity.ok(ApiResponse.onSuccess(alarms));
    }

    @GetMapping("/{alarm_id}")
    @Operation(summary = "특정 알림 조회", description = "사용자의 특정 알림을 조회합니다.")
    public ResponseEntity<ApiResponse<AlarmResponseDTO.AlarmDetailDTO>> getAlarmDetail(@PathVariable("alarm_id") Long alarmId) {
        AlarmResponseDTO.AlarmDetailDTO alarmDetailDTO = alarmQueryService.getAlarmDetail(alarmId);
        return ResponseEntity.ok(ApiResponse.onSuccess(alarmDetailDTO));
    }


}
