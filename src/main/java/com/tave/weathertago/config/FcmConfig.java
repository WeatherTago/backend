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

//    @PostConstruct
//    public void initialize() {
//        try {
//            // FileInputStream은 이미 InputStream이므로 바로 사용
//            InputStream serviceAccount = new FileInputStream("src/main/resources");
//
//            FirebaseOptions options = FirebaseOptions.builder()
//                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
//                    .build();
//
//            FirebaseApp.initializeApp(options);
//            log.info("Fcm 설정 성공");
//        } catch (IOException exception) {
//            log.error("Fcm 연결 오류 {}", exception.getMessage());
//        }
//    }
}