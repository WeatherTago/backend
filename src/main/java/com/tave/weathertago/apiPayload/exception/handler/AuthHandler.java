package com.tave.weathertago.apiPayload.exception.handler;

import com.tave.weathertago.apiPayload.code.BaseErrorCode;
import com.tave.weathertago.apiPayload.exception.GeneralException;

public class AuthHandler extends GeneralException {

    public AuthHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}