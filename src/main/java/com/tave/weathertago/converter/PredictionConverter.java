package com.tave.weathertago.converter;


import com.tave.weathertago.domain.Station;
import com.tave.weathertago.dto.prediction.AiServerResponseDTO;
import com.tave.weathertago.dto.prediction.PredictionRequestDTO;
import com.tave.weathertago.dto.prediction.PredictionResponseDTO;
import com.tave.weathertago.dto.weather.WeatherResponseDTO;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PredictionConverter {

    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public static PredictionRequestDTO toPredictionRequest(Station station, int direction, LocalDateTime datetime, WeatherResponseDTO weather) {
        return PredictionRequestDTO.builder()
                .line(station.getLine())
                .station_name(station.getName())
                .datetime(datetime.format(DATETIME_FMT))
                .direction(direction)
                .TMP(weather.getTmp())
                .REH(weather.getReh())
                .PCP(weather.getPcp())
                .WSD(weather.getWsd())
                .SNO(weather.getSno())
                .VEC(weather.getVec())
                .build();
    }

    public static PredictionResponseDTO toPredictionResponse(AiServerResponseDTO aiResponse) {
        return PredictionResponseDTO.builder()
                .congestionLevel(aiResponse.getResult().getPredictedCongestionLevel())
                .congestionScore(aiResponse.getResult().getPredictedCongestionScore())
                .build();
    }
}