package com.tave.weathertago.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Configuration
@Slf4j
public class FcmConfig {

    @Bean
    public FirebaseMessaging firebaseMessaging() {
        try (InputStream serviceAccount = getClass().getResourceAsStream("/weathertago-firebase-adminsdk-fbsvc-4885ca4b1e.json")) {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                log.info("Fcm 설정 성공");
            } else {
                log.info("FirebaseApp이 이미 초기화되어 있음");
            }
            return FirebaseMessaging.getInstance();
        } catch (Exception e) {
            log.error("Fcm 연결 오류: {}", e.getMessage());
            throw new IllegalStateException("Fcm 초기화 실패", e);
        }
    }

}