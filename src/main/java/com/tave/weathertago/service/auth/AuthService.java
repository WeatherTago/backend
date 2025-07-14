package com.tave.weathertago.service.auth;

import com.tave.weathertago.dto.Auth.AuthRequestDTO;
import com.tave.weathertago.dto.Auth.AuthResponseDTO;

public interface AuthService {
    AuthResponseDTO.LoginResultDTO kakaoLogin(String kakaoAccessToken);
    AuthResponseDTO.ReissueResultDTO reissueToken(AuthRequestDTO.ReissueRequest request);
    void logout(String accessToken);
}