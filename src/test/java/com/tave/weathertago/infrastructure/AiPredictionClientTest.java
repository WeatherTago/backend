package com.tave.weathertago.infrastructure;

import com.tave.weathertago.dto.prediction.PredictionResponseDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

@SpringBootTest
class AiPredictionClientTest {

    @Autowired
    private AiPredictionClient aiPredictionClient;

    @Test
    void 혼잡도_예측_테스트() {
        String line = "2호선";
        String stationName = "홍대입구";
        LocalDateTime baseTime = LocalDateTime.of(2025, 7, 6, 6, 0);

        for (int i = 0; i < 6; i++) {
            LocalDateTime datetime = baseTime.plusHours(i);
            try {
                long start = System.currentTimeMillis(); // 시작 시간 측정
                PredictionResponseDTO response = aiPredictionClient.predictCongestion(line, stationName, datetime);
                long end = System.currentTimeMillis();   // 종료 시간 측정
                long elapsed = end - start;              // 경과 시간 (ms)
                System.out.println("✅ [" + datetime + "] 혼잡도 예측 결과 → Level: "
                        + response.getPredictedCongestionLevel()
                        + ", Score: "
                        + response.getPredictedCongestionScore()
                        + " | ⏱ 응답 시간: " + elapsed + "ms");
            } catch (Exception e) {
                System.out.println("❌ [" + datetime + "] 혼잡도 예측 실패: " + e.getMessage());
            }
        }
    }
}