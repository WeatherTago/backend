package com.tave.weathertago.converter;

import com.tave.weathertago.dto.weather.WeatherInternalDTO;
import com.tave.weathertago.dto.weather.WeatherResponseDTO;

public class WeatherConverter {

    public static WeatherResponseDTO toResponseDTO(WeatherInternalDTO dto) {
        return WeatherResponseDTO.builder()
                .tmp(dto.getTmp())
                .reh(dto.getReh())
                .pcp(dto.getPcp())
                .wsd(dto.getWsd())
                .sno(dto.getSno())
                .vec(dto.getVec())
                .status(resolveStatus(dto.getPty(), dto.getSky()))
                .build();
    }

    private static String resolveStatus(int pty, int sky) {
        return switch (pty) {
            case 1, 4 -> "비";
            case 2 -> "눈비";
            case 3 -> "눈";
            default -> resolveSkyStatus(sky);
        };
    }

    private static String resolveSkyStatus(int sky) {
        return switch (sky) {
            case 1 -> "맑음";
            case 2, 3 -> "구름많음";
            case 4 -> "흐림";
            default -> "정보 없음";
        };
    }
}