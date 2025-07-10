package com.tave.weathertago.dto.prediction;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AiServerResponseDTO {
    private String status;
    private String congestion_level;
    private double congestion_score;
    private double total_time_sec;
    private Result result;

    @Getter
    @NoArgsConstructor
    public static class Result {
        private String line;
        private String station_name;
        private String datetime;
        private int direction;
        private double TMP;
        private double REH;
        private double PCP;
        private double WSD;
        private double SNO;
        private double VEC;
        private int year;
        private int month;
        private int day;
        private int hour;
        private int season;
        private int weekend;
        private double discomfort;
        @JsonProperty("predicted_congestion_level")
        private String predictedCongestionLevel;
        @JsonProperty("predicted_congestion_score")
        private double predictedCongestionScore;

    }
}