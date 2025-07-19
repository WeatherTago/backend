package com.tave.weathertago.service.user;

import com.tave.weathertago.apiPayload.code.status.ErrorStatus;
import com.tave.weathertago.apiPayload.exception.handler.UserHandler;
import com.tave.weathertago.security.jwt.JwtTokenProvider;
import com.tave.weathertago.domain.User;
import com.tave.weathertago.repository.AlarmRepository;
import com.tave.weathertago.repository.FavoriteRepository;
import com.tave.weathertago.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UserCommandServiceImpl implements UserCommandService {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final FavoriteRepository favoriteRepository;
    private final AlarmRepository alarmRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    @Transactional
    public void deleteUser(String accessToken) {
        jwtTokenProvider.validateToken(accessToken);
        String kakaoId = jwtTokenProvider.getKakaoId(accessToken);

        User user = userRepository.findByKakaoId(kakaoId)
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));

        // 2. 사용자 삭제
        userRepository.delete(user);

        // 토큰 삭제
        redisTemplate.delete("refresh:" + user.getId());
        String blacklistKey = "blacklist:" + accessToken;
        long remaining = jwtTokenProvider.getTokenRemainingTime(accessToken);
        redisTemplate.opsForValue().set(blacklistKey, "withdrawn", remaining, TimeUnit.MILLISECONDS);
    }
}