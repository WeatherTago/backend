package com.tave.weathertago.dto.prediction;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PredictionRequestDTO {
    private String line;
    private String station_name;
    private String datetime;
    private int direction;

    @JsonProperty("TMP")
    private double TMP;

    @JsonProperty("REH")
    private double REH;

    @JsonProperty("PCP")
    private double PCP;

    @JsonProperty("WSD")
    private double WSD;

    @JsonProperty("SNO")
    private double SNO;

    @JsonProperty("VEC")
    private double VEC;
}