package com.tave.weathertago.controller;

import com.tave.weathertago.apiPayload.ApiResponse;
import com.tave.weathertago.dto.Auth.AuthRequestDTO;
import com.tave.weathertago.dto.Auth.AuthResponseDTO;
import com.tave.weathertago.service.auth.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth", description = "카카오 소셜 로그인 및 JWT 토큰 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "카카오 로그인", description = "카카오 accessToken으로 로그인/회원가입을 처리하고 JWT를 발급합니다.")
    public ApiResponse<AuthResponseDTO.LoginResultDTO> login(@RequestBody @Valid AuthRequestDTO.KakaoLoginRequest request) {
        return ApiResponse.onSuccess(authService.kakaoLogin(request.getAccessToken()));
    }

    @PostMapping("/reissue")
    @Operation(summary = "토큰 재발급", description = "refreshToken으로 accessToken을 재발급합니다.")
    public ApiResponse<AuthResponseDTO.ReissueResultDTO> reissue(@RequestBody @Valid AuthRequestDTO.ReissueRequest request) {
        return ApiResponse.onSuccess(authService.reissueToken(request));
    }
}