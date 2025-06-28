package com.tave.weathertago.controller;

import com.tave.weathertago.apiPayload.ApiResponse;
import com.tave.weathertago.dto.user.UserInfoResponseDTO;
import com.tave.weathertago.service.user.UserQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User", description = "유저 관련 API")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserRestController {

    private final UserQueryService userQueryService;

    @GetMapping("/me")
    @Operation(summary = "내 정보 조회", description = "로그인한 사용자의 닉네임과 이메일을 반환합니다.")
    public ApiResponse<UserInfoResponseDTO> getMyInfo() {
        return ApiResponse.onSuccess(userQueryService.getMyInfo());
    }
}
