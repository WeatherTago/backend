package com.tave.weathertago.dto.Auth;

import lombok.Builder;
import lombok.Getter;

public class AuthResponseDTO {

    @Getter
    @Builder
    public static class LoginResultDTO {
        private Long userId;
        private String accessToken;
        private String refreshToken;
        private boolean isNewUser;
    }

    @Getter
    @Builder
    public static class ReissueResultDTO {
        private String accessToken;
        private String refreshToken;
    }
}