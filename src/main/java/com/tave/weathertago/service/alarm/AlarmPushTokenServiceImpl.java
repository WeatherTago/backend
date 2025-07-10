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

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlarmPushTokenServiceImpl implements AlarmPushTokenService{

    private final String REDIS_KEY_PREFIX = "pushtoken:";
    private final RedisTemplate<String,String> redisTemplate;
    private final UserRepository userRepository;

    // PushToken 추가
    @Override
    public void addPushToken(String pushToken){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String kakaoId = authentication.getName();

        User user = userRepository.findByKakaoId(kakaoId)
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));

        String key = REDIS_KEY_PREFIX + user.getId();
        redisTemplate.opsForSet().add(key, pushToken);
    }

    // PushToken 삭제 (로그아웃 시)
    @Override
    public void removePushToken(String pushToken){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String kakaoId = authentication.getName();

        User user = userRepository.findByKakaoId(kakaoId)
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));

        String key = REDIS_KEY_PREFIX + user.getId();
        redisTemplate.opsForSet().remove(key, pushToken);
    }

    // 해당 사용자의 모든 PushToken 조회
    @Override
    public Set<String> getPushTokens(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String kakaoId = authentication.getName();

        User user = userRepository.findByKakaoId(kakaoId)
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));

        String key = REDIS_KEY_PREFIX + user.getId();
        return redisTemplate.opsForSet().members(key);

    }

    // 모든 PushToken 삭제 (회원탈퇴 등)
    @Override
    public void removeAllPushTokens(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String kakaoId = authentication.getName();

        User user = userRepository.findByKakaoId(kakaoId)
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));

        String key = REDIS_KEY_PREFIX + user.getId();
        redisTemplate.delete(key);
    }
}
