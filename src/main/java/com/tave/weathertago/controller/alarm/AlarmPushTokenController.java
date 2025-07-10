package com.tave.weathertago.controller.alarm;

import com.tave.weathertago.apiPayload.ApiResponse;
import com.tave.weathertago.service.alarm.AlarmPushTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@Tag(name = "AlarmPushToken" , description = "알림 pushToken API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/me/pushtokens")
public class AlarmPushTokenController {

    private final AlarmPushTokenService alarmPushTokenService;

    // PushToken 추가
    @Operation(summary = "pushToken 추가", description = "로그인 시 사용자 ID에 해당하는 PushToken을 추가합니다.")
    @PostMapping("")
    public ResponseEntity<ApiResponse<Void>> addPushToken(
            @RequestParam String pushToken) {
        alarmPushTokenService.addPushToken(pushToken);
        return ResponseEntity.ok(ApiResponse.onSuccess(null));
    }

    // PushToken 삭제 (로그아웃 시)
    @Operation(summary = "pushToken 삭제", description = "로그아웃 시 사용자 ID에 해당하는 PushToken을 삭제합니다.")
    @DeleteMapping("")
    public ResponseEntity<ApiResponse<Void>> removePushToken(
            @RequestParam String pushToken) {
        alarmPushTokenService.removePushToken(pushToken);
        return ResponseEntity.ok(ApiResponse.onSuccess(null));
    }

    // 해당 사용자의 모든 PushToken 조회
    @Operation(summary = "pushToken 전체 조회", description = "사용자 ID에 해당하는 모든 기기의 PushToken을 조회합니다.")
    @GetMapping("")
    public ResponseEntity<ApiResponse<Set<String>>> getPushTokens() {
        Set<String> tokens = alarmPushTokenService.getPushTokens();
        return ResponseEntity.ok(ApiResponse.onSuccess(tokens));
    }

    // 모든 PushToken 삭제 (회원탈퇴 등)
    @Operation(summary = "pushToken 전체 삭제", description = "회원 탈퇴 시 사용자 ID에 해당하는 모든 기기의 PushToken을 삭제합니다.")
    @DeleteMapping("/all")
    public ResponseEntity<ApiResponse<Void>> removeAllPushTokens() {
        alarmPushTokenService.removeAllPushTokens();
        return ResponseEntity.ok(ApiResponse.onSuccess(null));
    }
}
