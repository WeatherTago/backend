package com.tave.weathertago.controller.alarm;

import com.tave.weathertago.apiPayload.ApiResponse;
import com.tave.weathertago.dto.alarm.AlarmRequestDTO;
import com.tave.weathertago.dto.alarm.AlarmResponseDTO;
import com.tave.weathertago.service.alarm.AlarmCommandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Alarm")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/me/alarms")
public class AlarmController {

    private final AlarmCommandService alarmCommandService;

    // 알람 생성
    @Operation(summary = "알람 생성", description = "알람을 생성합니다.")
    @PostMapping("")
    public ResponseEntity<ApiResponse<AlarmResponseDTO.AlarmDetailDTO>> createAlarm(@RequestBody AlarmRequestDTO.AlarmCreateRequestDTO dto) {
        return alarmCommandService.createAlarm(dto)
                .map(alarmDetail -> ResponseEntity.ok(ApiResponse.onSuccess(alarmDetail)))
                .orElse(ResponseEntity.badRequest().build());
    }

    // 알람 수정
    @Operation(summary = "알람 수정", description = "알람을 수정합니다.")
    @PatchMapping("/{alarm_id}")
    public ResponseEntity<ApiResponse<Void>> updateAlarm(@RequestBody AlarmRequestDTO.AlarmUpdateRequestDTO dto) {
        alarmCommandService.updateAlarm(dto);
        return ResponseEntity.ok(ApiResponse.onSuccess(null));
    }

    // 알람 삭제
    @Operation(summary = "알람 삭제", description = "알람을 삭제합니다.")
    @DeleteMapping("/{alarm_id}")
    public ResponseEntity<ApiResponse<Void>> deleteAlarm(@PathVariable Long alarmId) {
        alarmCommandService.deleteAlarm(alarmId);
        return ResponseEntity.ok(ApiResponse.onSuccess(null));
    }
}
