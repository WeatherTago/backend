package com.tave.weathertago.dto.prediction;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PredictionRequestDTO {
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
}