package com.tave.weathertago.config.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tave.weathertago.apiPayload.code.ErrorReasonDTO;
import com.tave.weathertago.apiPayload.exception.GeneralException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper;

    private static final List<String> EXCLUDED_PATHS = List.of(
            "/api/auth/login",
            "/api/auth/reissue"
    );

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return EXCLUDED_PATHS.contains(path);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String token = jwtTokenProvider.resolveToken(request);
            if (token != null) {
                jwtTokenProvider.validateToken(token);
                Authentication authentication = jwtTokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            filterChain.doFilter(request, response);
        } catch (GeneralException e) {
            ErrorReasonDTO reason = e.getErrorReasonHttpStatus();

            response.setStatus(reason.getHttpStatus().value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding("UTF-8");

            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("isSuccess", reason.getIsSuccess());
            responseBody.put("code", reason.getCode());
            responseBody.put("message", reason.getMessage());

            objectMapper.writeValue(response.getWriter(), responseBody);
        }
    }
}