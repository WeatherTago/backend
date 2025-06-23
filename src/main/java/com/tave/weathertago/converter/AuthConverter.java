package com.tave.weathertago.converter;

import com.tave.weathertago.dto.Auth.AuthResponseDTO;

public class AuthConverter {

    public static AuthResponseDTO.LoginResultDTO toLoginResultDTO(Long userId, String accessToken, String refreshToken, boolean isNewUser) {
        return AuthResponseDTO.LoginResultDTO.builder()
                .userId(userId)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .isNewUser(isNewUser)
                .build();
    }

    public static AuthResponseDTO.ReissueResultDTO toReissueResultDTO(String newAccessToken) {
        return AuthResponseDTO.ReissueResultDTO.builder()
                .accessToken(newAccessToken)
                .build();
    }
}
