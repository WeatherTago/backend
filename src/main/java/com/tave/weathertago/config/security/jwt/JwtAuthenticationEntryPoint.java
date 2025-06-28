package com.tave.weathertago.config.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tave.weathertago.apiPayload.code.status.ErrorStatus;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException)
            throws IOException {
        response.setStatus(ErrorStatus._UNAUTHORIZED.getHttpStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("isSuccess", false);
        responseBody.put("code", ErrorStatus._UNAUTHORIZED.getCode());
        responseBody.put("message", ErrorStatus._UNAUTHORIZED.getMessage());

        objectMapper.writeValue(response.getOutputStream(), responseBody);
    }
}