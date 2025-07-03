package com.tave.weathertago.apiPayload.exception.handler;

import com.tave.weathertago.apiPayload.code.BaseErrorCode;
import com.tave.weathertago.apiPayload.exception.GeneralException;

public class WeatherHandler extends GeneralException {

    public WeatherHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}