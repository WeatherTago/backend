package com.tave.weathertago.infrastructure;

import com.tave.weathertago.apiPayload.code.status.ErrorStatus;
import com.tave.weathertago.apiPayload.exception.GeneralException;
import com.tave.weathertago.dto.Auth.KakaoUserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class KakaoApiClient {

    private static final String KAKAO_USER_INFO_URL = "https://kapi.kakao.com/v2/user/me";

    private final RestTemplate restTemplate = new RestTemplate();

    public KakaoUserInfo getUserInfo(String accessToken) {
        // 1. Authorization 헤더 구성
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // 2. 요청 객체 생성
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        // 3. 요청 전송
        try {
            ResponseEntity<KakaoUserInfo> response = restTemplate.exchange(
                    KAKAO_USER_INFO_URL,
                    HttpMethod.GET,
                    entity,
                    KakaoUserInfo.class
            );
            return response.getBody();
        } catch (HttpClientErrorException.Unauthorized e) {
            // Kakao access token이 유효하지 않을 때
            throw new GeneralException(ErrorStatus.INVALID_TOKEN);
        } catch (Exception e) {
            throw new GeneralException(ErrorStatus._INTERNAL_SERVER_ERROR);
        }
    }
}