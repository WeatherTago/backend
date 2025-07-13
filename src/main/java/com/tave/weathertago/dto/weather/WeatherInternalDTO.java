package com.tave.weathertago.dto.weather;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeatherInternalDTO implements Serializable {
    private double tmp;
    private double reh;
    private double pcp;
    private double wsd;
    private double sno;
    private double vec;
    private int sky;
    private int pty;
}