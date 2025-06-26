package com.tave.weathertago.controller.alarm;

import com.tave.weathertago.dto.fcm.AlarmFcmMessageDto;
import com.tave.weathertago.service.alarm.AlarmCommandService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "alarm fcm controller", description = "알림 전송 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/alarm")
public class AlarmFcmController {
    private final AlarmCommandService alarmFcmService;

    @PostMapping("/send")
    public ResponseEntity<String> sendAlarm(@RequestBody AlarmFcmMessageDto dto) {
        alarmFcmService.sendAlarm(dto);
        return ResponseEntity.ok("알림 전송 완료");
    }
}
