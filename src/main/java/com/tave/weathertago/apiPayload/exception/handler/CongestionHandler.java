package com.tave.weathertago.apiPayload.exception.handler;

import com.tave.weathertago.apiPayload.code.BaseErrorCode;
import com.tave.weathertago.apiPayload.exception.GeneralException;

public class CongestionHandler extends GeneralException {

    public CongestionHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}