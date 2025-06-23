package com.tave.weathertago.dto.Auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

public class AuthRequestDTO {

    @Getter
    public static class KakaoLoginRequest {
        @NotBlank
        private String accessToken;
    }

    @Getter
    public static class ReissueRequest {
        @NotBlank
        private String refreshToken;
    }
}