package com.tave.weathertago.apiPayload.exception.handler;

import com.tave.weathertago.apiPayload.code.BaseErrorCode;
import com.tave.weathertago.apiPayload.exception.GeneralException;

public class StationHandler extends GeneralException {

    public StationHandler(BaseErrorCode errorCode){
        super(errorCode);
    }
}
