package com.tave.weathertago.converter;


import com.tave.weathertago.dto.prediction.PredictionRequestDTO;
import com.tave.weathertago.dto.prediction.PredictionResponseDTO;
import com.tave.weathertago.dto.weather.WeatherResponseDTO;

import java.time.LocalDateTime;

public class PredictionConverter {

    public static PredictionRequestDTO toPredictionRequest(String line, String stationName, LocalDateTime datetime, WeatherResponseDTO weather) {
        return PredictionRequestDTO.builder()
                .line(line)
                .station_name(stationName)
                .datetime(datetime.toString())
                .TMP(weather.getTmp())
                .REH(weather.getReh())
                .PCP(weather.getPcp())
                .WSD(weather.getWsd())
                .SNO(weather.getSno())
                .VEC(weather.getVec())
                .build();
    }

    public static PredictionResponseDTO toPredictionResponse(String line, String stationName, LocalDateTime datetime, String level, double score) {
        return PredictionResponseDTO.builder()
                .line(line)
                .stationName(stationName)
                .datetime(datetime)
                .predictedCongestionLevel(level)
                .predictedCongestionScore(score)
                .build();
    }
}