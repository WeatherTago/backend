package com.tave.weathertago.controller.alarm;

import com.tave.weathertago.apiPayload.ApiResponse;
import com.tave.weathertago.dto.alarm.AlarmFcmMessageDto;
import com.tave.weathertago.service.alarm.AlarmCommandService;
import com.tave.weathertago.service.alarm.AlarmSendService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Alarm auto send", description = "알림 전송 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/me/alarms")
public class AlarmFcmController {
    private final AlarmSendService alarmSendService;

    @Operation(summary = "알람 전송", description = "사용자가 설정한 시간에 맞추어 알람을 전송합니다.")
    @PostMapping("/{alarm_id}/send")
    public ResponseEntity<ApiResponse<AlarmFcmMessageDto>> sendAlarm(@PathVariable Long alarmId) {
        try {
            AlarmFcmMessageDto dto = alarmSendService.sendAlarm(alarmId);
            return ResponseEntity.ok(ApiResponse.onSuccess(dto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.onFailure("ERROR", e.getMessage(), null));
        }
    }

}
