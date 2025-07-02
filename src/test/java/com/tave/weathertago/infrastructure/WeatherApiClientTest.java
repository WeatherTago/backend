package com.tave.weathertago.infrastructure;

import com.tave.weathertago.dto.weather.WeatherResponseDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest
public class WeatherApiClientTest {

    @Autowired
    private WeatherApiClient weatherApiClient;

    @Test
    void 날씨_API_실행_테스트() {
        // given
        String stationName = "이대";
        String line = "2호선";

        // when
        weatherApiClient.getAndCacheWeather(stationName, line);

        // then
        // 단순 실행 테스트이므로 결과는 로그로 확인
        System.out.println("✅ getAndCacheWeather 실행 완료");
    }
}