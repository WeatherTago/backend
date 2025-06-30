package com.tave.weathertago.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Configuration
@Slf4j
public class FcmConfig {

    @PostConstruct
    public void initialize() {
        try (InputStream serviceAccount = getClass().getResourceAsStream("/weathertago-firebase-adminsdk-fbsvc-4885ca4b1e.json")) {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {  // 초기화된 앱이 없으면 초기화
                FirebaseApp.initializeApp(options);
                log.info("Fcm 설정 성공");
            } else {
                log.info("FirebaseApp이 이미 초기화되어 있음");
            }
        } catch (IOException exception) {
            log.error("Fcm 연결 오류 {}", exception.getMessage());
        }
    }

}