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

    // ✅ API 기본 주소
    private static final String BASE_URL = "http://ws.bus.go.kr/api/rest/pathinfo/getPathInfoBySubway";


    @Value("${subwaypath.api.key}")
    private String serviceKey;
    // ✅ 인코딩된 상태의 서비스 키 (절대로 encode() 다시 하지 마세요!)

    public SubwayOpenApiClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public SubwayPathResponseDTO getPathInfo(double startX, double startY, double endX, double endY) {
        try {
            // ✅ 전체 인코딩된 URL 문자열 생성
            String fullUrl = String.format(
                    "%s?ServiceKey=%s&startX=%f&startY=%f&endX=%f&endY=%f",
                    BASE_URL, serviceKey, startX, startY, endX, endY
            );

            // ✅ URI 객체로 직접 생성 → RestTemplate이 인코딩을 추가로 하지 않게 됨
            URI uri = new URI(fullUrl);

            System.out.println("📡 호출 URI: " + uri);

            String xml = restTemplate.getForObject(uri, String.class);
            System.out.println("🧾 응답 원문: " + xml);

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
