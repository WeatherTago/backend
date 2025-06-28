package com.tave.weathertago.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.tave.weathertago.dto.Station.SubwayPathResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class SubwayOpenApiClient {

    private final RestTemplate restTemplate;

    private final String BASE_URL = "http://ws.bus.go.kr/api/rest/pathinfo/getPathInfoBySubway";


    private final String SERVICE_KEY = "3EVVs0L+wjAoRLCcklOJuag+ws/ubzKf4t92j1EucLCJRw1bJgIjmRHH5Ey+0QwW9zU3P9v6NT+s0OvRHpd8Fg==";

    public SubwayOpenApiClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public SubwayPathResponseDTO getPathInfo(double startX, double startY, double endX, double endY) {
        String uri = String.format(
                "%s?ServiceKey=%s&startX=%f&startY=%f&endX=%f&endY=%f",
                BASE_URL, SERVICE_KEY, startX, startY, endX, endY
        );

        try {
            System.out.println("📡 호출 URI: " + uri);

            String xml = restTemplate.getForObject(uri, String.class);
            System.out.println("🧾 응답 원문: " + xml);

            XmlMapper xmlMapper = new XmlMapper();
            SubwayPathResponseDTO dto = xmlMapper.readValue(xml, SubwayPathResponseDTO.class);

            if (dto == null || dto.getMsgHeader() == null || !"0".equals(dto.getMsgHeader().getHeaderCd())) {
                System.err.println("API 오류 또는 인증 실패: " +
                        (dto != null && dto.getMsgHeader() != null ? dto.getMsgHeader().getHeaderMsg() : "응답 없음"));
                return null;
            }

            return dto;

        } catch (Exception e) {
            System.err.println("❌ OpenAPI 호출 실패: " + e.getMessage());
            return null;
        }
    }
}
