package com.tave.weathertago.service.auth;

import com.tave.weathertago.apiPayload.code.status.ErrorStatus;
import com.tave.weathertago.apiPayload.exception.handler.UserHandler;
import com.tave.weathertago.config.security.jwt.JwtTokenProvider;
import com.tave.weathertago.converter.AuthConverter;
import com.tave.weathertago.domain.User;
import com.tave.weathertago.dto.Auth.AuthRequestDTO;
import com.tave.weathertago.dto.Auth.AuthResponseDTO;
import com.tave.weathertago.dto.Auth.KakaoUserInfo;
import com.tave.weathertago.infrastructure.KakaoApiClient;
import com.tave.weathertago.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final KakaoApiClient kakaoApiClient;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    @Transactional
    public AuthResponseDTO.LoginResultDTO kakaoLogin(String accessToken) {

        // 1. 카카오 사용자 정보 조회
        KakaoUserInfo kakaoUserInfo = kakaoApiClient.getUserInfo(accessToken);

        // 2. 사용자 정보로 회원 존재 여부 확인
        User user = findOrCreateUser(kakaoUserInfo);
        boolean isNewUser = user.getCreatedAt().equals(user.getUpdatedAt());

        // 3. JWT 발급
        String accessJwt = jwtTokenProvider.generateAccessToken(user.getKakaoId());
        String refreshJwt = jwtTokenProvider.generateRefreshToken(user.getKakaoId());
        user.updateRefreshToken(refreshJwt);

        // 4. 응답 생성
        return AuthConverter.toLoginResultDTO(user.getId(), accessJwt, refreshJwt, isNewUser);
    }

    @Override
    @Transactional
    public AuthResponseDTO.ReissueResultDTO reissueToken(AuthRequestDTO.ReissueRequest request) {

        jwtTokenProvider.validateToken(request.getRefreshToken());

        String kakaoId = jwtTokenProvider.getKakaoId(request.getRefreshToken());

        User user = userRepository.findByKakaoId(kakaoId)
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));

        if (!user.getRefreshToken().equals(request.getRefreshToken())) {
            throw new UserHandler(ErrorStatus.INVALID_TOKEN);
        }

        String newAccessToken = jwtTokenProvider.generateAccessToken(user.getKakaoId());
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(kakaoId);

        user.updateRefreshToken(newRefreshToken);

        return AuthConverter.toReissueResultDTO(newAccessToken, newRefreshToken);
    }

    private User findOrCreateUser(KakaoUserInfo kakaoUserInfo) {
        return userRepository.findByKakaoId(kakaoUserInfo.getKakaoId())
                .orElseGet(() -> userRepository.save(User.builder()
                        .kakaoId(kakaoUserInfo.getKakaoId())
                        .email(kakaoUserInfo.getEmail())
                        .nickname(kakaoUserInfo.getNickname())
                        .build()));
    }
}
