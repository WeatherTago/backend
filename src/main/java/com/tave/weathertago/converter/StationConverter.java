package com.tave.weathertago.converter;

import com.tave.weathertago.domain.Station;
import com.tave.weathertago.dto.prediction.PredictionResponseDTO;
import com.tave.weathertago.dto.station.StationResponseDTO;
import com.tave.weathertago.dto.weather.WeatherResponseDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class StationConverter {

    public static StationResponseDTO.JoinResultDTO toJoinResultDTO(Station station, WeatherResponseDTO weather,  Map<String, StationResponseDTO.DirectionalStationDTO> congestionByDirection) {

        return StationResponseDTO.JoinResultDTO.builder()
                .stationId(station.getId())
                .name(station.getName())
                .line(station.getLine())
                .stationCode(station.getStationCode())
                .weather(weather)
                .congestionByDirection(congestionByDirection)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static StationResponseDTO.DirectionalStationDTO toDirectionalStationDTO(Station station, PredictionResponseDTO congestion) {

        return StationResponseDTO.DirectionalStationDTO.builder()
                .stationId(station.getId())
                .congestion(congestion)
                .build();
    }

    public static StationResponseDTO.SimpleStationDTO toSimpleDTO(Station station) {
        return StationResponseDTO.SimpleStationDTO.builder()
                .stationId(station.getId())
                .stationName(station.getName())
                .stationLine(station.getLine())
                .build();
    }

    public static StationResponseDTO.StationInfoDTO toStationInfoDTO(Station station) {
        return StationResponseDTO.StationInfoDTO.builder()
                .stationId(station.getId())
                .stationName(station.getName())
                .stationLine(station.getLine())
                .phoneNumber(station.getPhoneNumber())
                .address(station.getAddress())
                .build();
    }

    public static StationResponseDTO.DirectionalData toDirectionalDataDTO(
            List<StationResponseDTO.TimedWeatherDTO> weathers,
            List<StationResponseDTO.TimedCongestionDTO> congestions
    ) {
        return StationResponseDTO.DirectionalData.builder()
                .weathers(weathers)
                .congestions(congestions)
                .build();
    }

}
