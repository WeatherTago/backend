package com.tave.weathertago.config.security.jwt;

import com.tave.weathertago.apiPayload.code.status.ErrorStatus;
import com.tave.weathertago.apiPayload.exception.GeneralException;
import com.tave.weathertago.config.security.properties.Constants;
import com.tave.weathertago.config.security.properties.JwtProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Collections;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtProperties jwtProperties;

    // 시크릿 키 바이트 배열로 변환
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(jwtProperties.getSecretKey().getBytes());
    }

    // 사용자 kakaoId를 token 생성
    public String generateAccessToken(String kakaoId) {
        return Jwts.builder()
                .setSubject(kakaoId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtProperties.getExpiration().getAccess()))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(String kakaoId) {
        return Jwts.builder()
                .setSubject(kakaoId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtProperties.getExpiration().getRefresh()))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // JWT 유효성 검사
    public void validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
        } catch (ExpiredJwtException e) {
            throw new GeneralException(ErrorStatus.EXPIRED_TOKEN);
        } catch (MalformedJwtException e) {
            throw new GeneralException(ErrorStatus.MALFORMED_TOKEN);
        } catch (SecurityException | SignatureException e) {
            throw new GeneralException(ErrorStatus.INVALID_SIGNATURE);
        } catch (JwtException | IllegalArgumentException e) {
            throw new GeneralException(ErrorStatus.INVALID_TOKEN);
        }
    }

    // jwt에서 kakao id 꺼내기 -> sub에 저장
    public String getKakaoId(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    // Request 헤더에서 순수 토큰 추출
    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(Constants.AUTH_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(Constants.TOKEN_PREFIX)) {
            return bearerToken.substring(Constants.TOKEN_PREFIX.length());
        }
        return null;
    }


    // jwt로부터 Spring Security 인증 객체 생성
    public Authentication getAuthentication(String token) {
        String kakaoId = getKakaoId(token);
        User principal = new User(kakaoId, "", Collections.emptyList());
        return new UsernamePasswordAuthenticationToken(principal, token, principal.getAuthorities());
    }

    // 인증 객체 추출 (Spring Security의 Authentication 객체 생성)
    public Authentication extractAuthentication(HttpServletRequest request) {
        String token = resolveToken(request);
        if (token == null) {
            throw new GeneralException(ErrorStatus._UNAUTHORIZED);
        }
        validateToken(token);

        String kakaoId = getKakaoId(token);
        User principal = new User(kakaoId, "", Collections.emptyList());
        return new UsernamePasswordAuthenticationToken(principal, token, principal.getAuthorities());
    }
}