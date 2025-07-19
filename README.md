## 🌤️웨더타고 | 교통 약자를 위한 지하철 혼잡도 안내 서비스

## 1. Project Introduction


## 2. Tech Stack
<div>
<img src="https://img.shields.io/badge/Java-007396?style=flat-square&logo=java&logoColor=white">
<img src="https://img.shields.io/badge/Gradle-02303A?style=flat-square&logo=gradle&logoColor=white">
<img src="https://img.shields.io/badge/Spring Boot-6DB33F?style=flat-square&logo=springboot&logoColor=white">
<img src="https://img.shields.io/badge/Spring Data JPA-6DB33F?style=flat-square&logo=databricks&logoColor=white">
<img src="https://img.shields.io/badge/Spring Security-6DB33F?style=flat-square&logo=springsecurity&logoColor=white">
<img src="https://img.shields.io/badge/Jsoup-6DB33F?style=flat-square&logo=jsoup&logoColor=white">
<img src="https://img.shields.io/badge/Firebase-DD2C00?style=flat-square&logo=firebase&logoColor=white">
<img src="https://img.shields.io/badge/OAuth2.0-000000?style=flat-square&logoColor=white">
</div>
<div>
<img src="https://img.shields.io/badge/MySQL-4479A1?style=flat-square&logo=mysql&logoColor=white">
<img src="https://img.shields.io/badge/Redis-FF4438?style=flat-square&logo=redis&logoColor=white">
</div>
<div>
<img src="https://img.shields.io/badge/Github Actions-2088FF?style=flat-square&logo=githubactions&logoColor=white">
<img src="https://img.shields.io/badge/Docker-2496ED?style=flat-square&logo=docker&logoColor=white">
<img src="https://img.shields.io/badge/AWS EC2-FF9900?style=flat-square&logo=amazonec2&logoColor=white">
<img src="https://img.shields.io/badge/AWS ALB-FF9900?style=flat-square&logo=awselasticloadbalancing&logoColor=white">
<img src="https://img.shields.io/badge/AWS CloudFront-FF9900?style=flat-square&logo=amazoncloudfront&logoColor=white">
<img src="https://img.shields.io/badge/AWS RDS-527FFF?style=flat-square&logo=amazonrds&logoColor=white">
<img src="https://img.shields.io/badge/AWS ElastiCache-C925D1?style=flat-square&logo=amazonelasticache&logoColor=white">
<img src="https://img.shields.io/badge/AWS Route53-FF9900?style=flat-square&logo=amazonroute53&logoColor=white">
<img src="https://img.shields.io/badge/AWS ACM-FF9900?style=flat-square&logo=awscertificatemanager&logoColor=white">
</div>

## 3. Architecture
<img width="3480" height="1806" alt="weathertago_cloud_architecture drawio" src="https://github.com/user-attachments/assets/48e83d00-e741-4b94-90d4-70fdcd50406b" />

## 4. Directory Structure
```
📦 src/main/java/com/tave/weathertago
├── 📁 apiPayload         # 공통 응답, 에러 코드, 예외 처리 등 API 응답 관련 패키지
├── 📁 config             # 스프링 설정 클래스
├── 📁 controller         # REST API 컨트롤러 계층
├── 📁 converter          # Entity <-> DTO 변환 책임 클래스
├── 📁 domain             # 핵심 도메인 Entity 클래스
├── 📁 dto                # 요청 및 응답에 사용되는 DTO 클래스
├── 📁 infrastructure     # 외부 시스템 연동
├── 📁 repository         # JPA 및 커스텀 쿼리 Repository 인터페이스
├── 📁 security           # JWT 필터, 인증 처리, Security 설정 등 보안 관련 코드
├── 📁 service            # 비즈니스 로직 계층
└── 📄 WeathertagoApplication.java
```
## 5. Developers
| 전지원 | 이원준 | 박유정 |
|:------:|:------:|:------:|
| <img src="https://github.com/jiwonly.png" alt="전지원" width="150"> | <img src="https://github.com/wonjun-lee-fcwj245.png" alt="이원준" width="150"> | <img src="https://github.com/yujeong430.png" alt="박유정" width="150"> |
| BE | BE | BE |
| [GitHub](https://github.com/jiwonly) | [GitHub](https://github.com/wonjun-lee-fcwj245) | [GitHub](https://github.com/yujeong430) |
