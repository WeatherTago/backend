package com.tave.weathertago.apiPayload.exception.handler;

import com.tave.weathertago.apiPayload.code.BaseErrorCode;
import com.tave.weathertago.apiPayload.exception.GeneralException;

public class NoticeHandler extends GeneralException {
    public NoticeHandler(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
