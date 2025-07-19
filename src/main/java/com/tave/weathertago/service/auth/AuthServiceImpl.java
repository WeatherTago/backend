package com.tave.weathertago.service.auth;

import com.tave.weathertago.apiPayload.code.status.ErrorStatus;
import com.tave.weathertago.apiPayload.exception.handler.AuthHandler;
import com.tave.weathertago.apiPayload.exception.handler.UserHandler;
import com.tave.weathertago.security.jwt.JwtTokenProvider;
import com.tave.weathertago.converter.AuthConverter;
import com.tave.weathertago.domain.User;
import com.tave.weathertago.dto.Auth.AuthRequestDTO;
import com.tave.weathertago.dto.Auth.AuthResponseDTO;
import com.tave.weathertago.dto.Auth.KakaoUserInfo;
import com.tave.weathertago.infrastructure.KakaoApiClient;
import com.tave.weathertago.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final KakaoApiClient kakaoApiClient;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    @Transactional
    public AuthResponseDTO.LoginResultDTO kakaoLogin(String accessToken) {

        // 카카오 사용자 정보 조회
        KakaoUserInfo kakaoUserInfo = kakaoApiClient.getUserInfo(accessToken);

        // 사용자 정보로 회원 존재 여부 확인
        Optional<User> optionalUser = userRepository.findByKakaoId(kakaoUserInfo.getKakaoId());
        boolean isNewUser = optionalUser.isEmpty();

        // 유저가 없으면 생성
        User user = optionalUser.orElseGet(() ->
                userRepository.save(User.builder()
                        .kakaoId(kakaoUserInfo.getKakaoId())
                        .email(kakaoUserInfo.getEmail())
                        .nickname(kakaoUserInfo.getNickname())
                        .build()));

        // JWT 발급
        String accessJwt = jwtTokenProvider.generateAccessToken(user.getKakaoId());
        String refreshJwt = jwtTokenProvider.generateRefreshToken(user.getKakaoId());
        saveRefreshToken(user.getId(), refreshJwt);

        // 응답 생성
        return AuthConverter.toLoginResultDTO(user.getId(), accessJwt, refreshJwt, isNewUser);
    }

    @Override
    @Transactional
    public AuthResponseDTO.ReissueResultDTO reissueToken(AuthRequestDTO.ReissueRequest request) {

        jwtTokenProvider.validateToken(request.getRefreshToken());
        log.info("[토큰 재발급] Refresh Token 유효성 검사 통과");

        String kakaoId = jwtTokenProvider.getKakaoId(request.getRefreshToken());

        User user = userRepository.findByKakaoId(kakaoId)
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));

        String key = "refresh:" + user.getId();
        Object cached = redisTemplate.opsForValue().get(key);

        if (!(cached instanceof String storedToken) || !storedToken.equals(request.getRefreshToken())) {
            log.error("[토큰 재발급] Redis에 저장된 Refresh Token과 일치하지 않음: userId={}", user.getId());
            throw new AuthHandler(ErrorStatus.INVALID_TOKEN);
        }

        String newAccessToken = jwtTokenProvider.generateAccessToken(user.getKakaoId());
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(kakaoId);

        saveRefreshToken(user.getId(), newRefreshToken);

        log.info("[토큰 재발급] 토큰 재발급 완료: userId={}", user.getId());
        return AuthConverter.toReissueResultDTO(newAccessToken, newRefreshToken);
    }

    @Override
    @Transactional
    public void logout(String accessToken) {
        jwtTokenProvider.validateToken(accessToken);
        String kakaoId = jwtTokenProvider.getKakaoId(accessToken);
        User user = userRepository.findByKakaoId(kakaoId)
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));

        // Refresh Token 삭제
        redisTemplate.delete("refresh:" + user.getId());
        log.info("[로그아웃] Refresh Token 삭제 완료: userId={}", user.getId());

        // Access Token Blacklist 등록
        String blacklistKey = "blacklist:" + accessToken;
        long remaining = jwtTokenProvider.getTokenRemainingTime(accessToken);
        redisTemplate.opsForValue().set(blacklistKey, "logout", remaining, TimeUnit.MILLISECONDS);
        log.info("[로그아웃] Access Token 블랙리스트 등록 완료: 남은 시간={}ms", remaining);
    }

    private void saveRefreshToken(Long userId, String refreshToken) {
        String key = "refresh:" + userId;
        long ttl = jwtTokenProvider.getRefreshTokenExpiration();

        redisTemplate.opsForValue().set(key, refreshToken, ttl, TimeUnit.MILLISECONDS);
        log.info("[토큰 저장] Refresh Token 저장 완료: userId={}, TTL={}ms", userId, ttl);
    }
}
