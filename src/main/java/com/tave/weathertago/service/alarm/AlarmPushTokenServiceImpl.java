package com.tave.weathertago.service.alarm;

import com.tave.weathertago.apiPayload.code.status.ErrorStatus;
import com.tave.weathertago.apiPayload.exception.handler.UserHandler;
import com.tave.weathertago.domain.User;
import com.tave.weathertago.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlarmPushTokenServiceImpl implements AlarmPushTokenService {

    private final String REDIS_KEY_PREFIX = "pushtoken:";
    private final RedisTemplate<String, String> redisTemplate;
    private final UserRepository userRepository;

    /**
     * PushToken 추가
     * URL 인코딩된 토큰을 디코딩하여 저장합니다.
     */
    @Override
    public void addPushToken(String pushToken) {
        // URL 디코딩 수행
        String decodedToken = decodeUrlString(pushToken);

        // 토큰 유효성 검증
        validatePushToken(decodedToken);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String kakaoId = authentication.getName();

        User user = userRepository.findByKakaoId(kakaoId)
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));

        String key = REDIS_KEY_PREFIX + user.getId();

        // 디코딩된 토큰으로 저장
        redisTemplate.opsForSet().add(key, decodedToken);

        log.info("Push Token 추가 완료: userId={}, token={}", user.getId(), decodedToken);
    }

    /**
     * PushToken 삭제 (로그아웃 시)
     * URL 인코딩된 토큰을 디코딩하여 삭제합니다.
     */
    @Override
    public void removePushToken(String pushToken) {
        // URL 디코딩 수행
        String decodedToken = decodeUrlString(pushToken);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String kakaoId = authentication.getName();

        User user = userRepository.findByKakaoId(kakaoId)
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));

        String key = REDIS_KEY_PREFIX + user.getId();

        // 디코딩된 토큰으로 삭제
        Long removedCount = redisTemplate.opsForSet().remove(key, decodedToken);

        if (removedCount > 0) {
            log.info("Push Token 삭제 완료: userId={}, token={}", user.getId(), decodedToken);
        } else {
            log.warn("Push Token 삭제 실패 (토큰 없음): userId={}, token={}", user.getId(), decodedToken);
        }
    }

    /**
     * 해당 사용자의 모든 PushToken 조회
     * Redis에 저장된 디코딩된 토큰들을 반환합니다.
     */
    @Override
    public Set<String> getPushTokens() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String kakaoId = authentication.getName();

        User user = userRepository.findByKakaoId(kakaoId)
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));

        String key = REDIS_KEY_PREFIX + user.getId();
        Set<String> tokens = redisTemplate.opsForSet().members(key);

        log.info("Push Token 조회 완료: userId={}, tokenCount={}", user.getId(),
                tokens != null ? tokens.size() : 0);

        return tokens;
    }

    /**
     * 모든 PushToken 삭제 (회원탈퇴 등)
     */
    @Override
    public void removeAllPushTokens() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String kakaoId = authentication.getName();

        User user = userRepository.findByKakaoId(kakaoId)
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));

        String key = REDIS_KEY_PREFIX + user.getId();
        Boolean deleted = redisTemplate.delete(key);

        log.info("모든 Push Token 삭제 완료: userId={}, deleted={}", user.getId(), deleted);
    }

    /**
     * URL 인코딩된 문자열을 디코딩합니다.
     * %5B -> [, %5D -> ] 변환을 수행합니다.
     */
    private String decodeUrlString(String encodedString) {
        if (encodedString == null || encodedString.trim().isEmpty()) {
            throw new IllegalArgumentException("Push Token이 비어있습니다.");
        }

        try {
            // URL 디코딩 수행
            String decoded = URLDecoder.decode(encodedString, StandardCharsets.UTF_8.name());
            log.debug("URL 디코딩 완료: {} -> {}", encodedString, decoded);
            return decoded;
        } catch (UnsupportedEncodingException e) {
            log.error("URL 디코딩 실패: {}", encodedString, e);
            throw new RuntimeException("Push Token 디코딩 실패", e);
        }
    }

    /**
     * Push Token의 유효성을 검증합니다.
     * Expo Push Token 형식인지 확인합니다.
     */
    private void validatePushToken(String pushToken) {
        if (pushToken == null || pushToken.trim().isEmpty()) {
            throw new IllegalArgumentException("Push Token이 비어있습니다.");
        }

        // Expo Push Token 형식 검증
        if (!pushToken.startsWith("ExponentPushToken[") || !pushToken.endsWith("]")) {
            throw new IllegalArgumentException("유효하지 않은 Expo Push Token 형식입니다: " + pushToken);
        }

        // 토큰 길이 검증 (너무 짧거나 긴 토큰 방지)
        if (pushToken.length() < 20 || pushToken.length() > 200) {
            throw new IllegalArgumentException("Push Token 길이가 유효하지 않습니다: " + pushToken.length());
        }

        log.debug("Push Token 유효성 검증 완료: {}", pushToken);
    }
}
