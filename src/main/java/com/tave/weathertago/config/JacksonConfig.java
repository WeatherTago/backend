package com.tave.weathertago.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.TimeZone;

@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule()); // ✅ LocalDateTime 모듈 등록
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // ✅ 문자열로 직렬화
        mapper.setTimeZone(TimeZone.getTimeZone("Asia/Seoul")); // ✅ 타임존 설정
        return mapper;
    }
}