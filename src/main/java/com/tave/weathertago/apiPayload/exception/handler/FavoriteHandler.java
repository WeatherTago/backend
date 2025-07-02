package com.tave.weathertago.apiPayload.exception.handler;

import com.tave.weathertago.apiPayload.code.BaseErrorCode;
import com.tave.weathertago.apiPayload.exception.GeneralException;

public class FavoriteHandler extends GeneralException{
    public FavoriteHandler(BaseErrorCode errorCode){super(errorCode);}
}
