package com.tave.weathertago.service.alarm;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.tave.weathertago.dto.fcm.AlarmFcmMessageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AlarmCommandServiceImpl implements AlarmCommandService {

        @Override
        public void sendAlarm (AlarmFcmMessageDto dto){
        // 알림 제목/본문 예시
        String title = "지하철 알림";
        String body = dto.getContent();

        // FCM 메시지 생성
        Message message = Message.builder()
                .setToken(dto.getPushToken())
                .putData("time", dto.getTime() != null ? dto.getTime().toString() : "")
                .putData("day", dto.getDay() != null ? dto.getDay() : "")
                .putData("content", dto.getContent() != null ? dto.getContent() : "")
                .putData("station", dto.getStation() != null ? dto.getStation() : "")
                .putData("weather", dto.getWeather() != null ? dto.getWeather() : "")
                .putData("title", title)
                .putData("body", body)
                .setNotification(Notification.builder()
                    .setTitle(title)
                    .setBody(body)
                    .build())
                .build();

        try {
            String response = FirebaseMessaging.getInstance().send(message);
            System.out.println("Successfully sent message: " + response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    }

