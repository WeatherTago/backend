package com.tave.weathertago.dto.station;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

//외부 API응답용
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class StationDTO {
    private String name;
    private String line;
    private String stationCode;
    private String direction;
    private Double latitude;
    private Double longitude;
}