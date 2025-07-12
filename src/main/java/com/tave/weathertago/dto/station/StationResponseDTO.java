package com.tave.weathertago.dto.station;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tave.weathertago.dto.prediction.PredictionResponseDTO;
import com.tave.weathertago.dto.weather.WeatherResponseDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;

public class StationResponseDTO {

    @Getter
    @Builder
    @AllArgsConstructor
    public static class JoinResultDTO{
        Long stationId;
        String name;
        String line;
        String stationCode;
        String direction;
        WeatherResponseDTO weather;
        @Schema(
                description = "방향별 혼잡도 정보",
                example = """
{
  "상행": {
    "stationId": 150,
    "congestion": {
      "congestionScore": 42.1,
      "congestionLevel": "보통"
    }
  },
  "하행": {
    "stationId": 150,
    "congestion": {
      "congestionScore": 67.8,
      "congestionLevel": "혼잡"
    }
  },
  "내선": {
    "stationId": 151,
    "congestion": {
      "congestionScore": 55.2,
      "congestionLevel": "보통"
    }
  },
  "외선": {
    "stationId": 152,
    "congestion": {
      "congestionScore": 35.7,
      "congestionLevel": "여유"
    }
  }
}
"""
        )
        Map<String, DirectionalStationDTO> congestionByDirection;
        @Schema(type = "string", format = "date-time", example = "2025-07-08T22:40:00")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
        LocalDateTime createdAt;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class DirectionalStationDTO{
        Long stationId;
        private PredictionResponseDTO congestion;
    }

    @Data
    @Builder
    public static class SimpleStationDTO {
        private Long stationId;
        private String stationName;
        private String stationLine;
    }
}
