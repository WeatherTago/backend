package com.tave.weathertago.infrastructure;


import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.tave.weathertago.dto.station.SubwayPathResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Component
public class SubwayOpenApiClient {

    private final RestTemplate restTemplate;

    private static final String BASE_URL = "http://ws.bus.go.kr/api/rest/pathinfo/getPathInfoBySubway";

    @Value("${subwaypath.api.key}")
    private String serviceKey;

    public SubwayOpenApiClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public SubwayPathResponseDTO getPathInfo(double startX, double startY, double endX, double endY) {
        try {
            // ✅ URL을 직접 String으로 구성 (이중 인코딩 방지)
            String fullUrl = BASE_URL +
                    "?ServiceKey=" + serviceKey +
                    "&startX=" + startX +
                    "&startY=" + startY +
                    "&endX=" + endX +
                    "&endY=" + endY;

            System.out.println("📡 호출 URL: " + fullUrl);

            // ✅ getForObject(String url, ...) 사용
            String xml = restTemplate.getForObject(fullUrl, String.class);
            System.out.println("🧾 응답 원문: " + xml);

            // ✅ HTML 오류 응답 방지용 검사
            if (xml != null && xml.contains("<html")) {
                throw new RuntimeException("HTML 페이지가 반환됨. 인증키 또는 파라미터 확인 필요.");
            }

            XmlMapper xmlMapper = new XmlMapper();
            SubwayPathResponseDTO dto = xmlMapper.readValue(xml, SubwayPathResponseDTO.class);

            if (dto == null || dto.getMsgHeader() == null) {
                System.err.println("❗ API 응답이 null이거나 msgHeader가 없습니다.");
                return null;
            }

            if (!"0".equals(dto.getMsgHeader().getHeaderCd())) {
                System.err.println("❗ API 오류: " + dto.getMsgHeader().getHeaderMsg());
                return null;
            }

            return dto;

        } catch (Exception e) {
            System.err.println("❌ OpenAPI 호출 실패: " + e.getMessage());
            return null;
        }
    }
}

