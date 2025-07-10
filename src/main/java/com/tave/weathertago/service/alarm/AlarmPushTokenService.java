package com.tave.weathertago.service.alarm;

import java.util.Set;

public interface AlarmPushTokenService {
    void addPushToken(String pushToken);
    void removePushToken(String pushToken);
    void removeAllPushTokens();
    Set<String> getPushTokens();
}
