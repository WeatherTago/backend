package com.tave.weathertago.apiPayload.exception.handler;

import com.tave.weathertago.apiPayload.code.BaseErrorCode;
import com.tave.weathertago.apiPayload.exception.GeneralException;

public class AlarmHandler extends GeneralException {
    public AlarmHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
