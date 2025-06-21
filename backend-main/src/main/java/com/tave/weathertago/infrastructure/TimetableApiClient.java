package com.tave.weathertago.infrastructure;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tave.weathertago.dto.TimeTableDTO;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

@Component
public class TimetableApiClient {

    private static final String BASE_URL = "http://openapi.seoul.go.kr:8088";
    private static final String API_KEY = "537978686a66637738366175754873";

    public List<TimeTableDTO> getTimetable(String stationCode, String weekTag, String inoutTag) {
        try {
            StringBuilder urlBuilder = new StringBuilder(BASE_URL);
            urlBuilder.append("/").append(URLEncoder.encode(API_KEY, "UTF-8"));
            urlBuilder.append("/").append("json");
            urlBuilder.append("/").append("SearchSTNTimeTableByIDService");
            urlBuilder.append("/").append("1");
            urlBuilder.append("/").append("1000");
            urlBuilder.append("/").append(URLEncoder.encode(stationCode, "UTF-8"));
            urlBuilder.append("/").append(URLEncoder.encode(weekTag, "UTF-8"));
            urlBuilder.append("/").append(URLEncoder.encode(inoutTag, "UTF-8"));

            URL url = new URL(urlBuilder.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader rd = new BufferedReader(new InputStreamReader(
                    conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300
                            ? conn.getInputStream()
                            : conn.getErrorStream()
            ));

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }

            rd.close();
            conn.disconnect();


            return parseTimeTable(sb.toString(), inoutTag);

        } catch (IOException e) {
            throw new RuntimeException("시간표 API 호출 실패", e);
        }
    }

    private List<TimeTableDTO> parseTimeTable(String json, String inoutTag) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(json);
            JsonNode rows = root.path("SearchSTNTimeTableByIDService").path("row");

            List<TimeTableDTO> timetable = new ArrayList<>();

            for (JsonNode row : rows) {
                String time = row.path("ARRIVETIME").asText();     // "20:22"
                String destination = row.path("SUBWAYENAME").asText();  // 도착지

                // 시간 추출 (시간 필터용)
                int hour = 0;
                if (time.length() >= 2) {
                    try {
                        hour = Integer.parseInt(time.substring(0, 2));
                    } catch (NumberFormatException ignored) {}
                }

                // 상하행 정보 추가
                String direction = inoutTag.equals("1") ? "상행" : "하행";

                TimeTableDTO dto = new TimeTableDTO(time, destination, direction, hour);
                timetable.add(dto);
            }

            return timetable;
        } catch (Exception e) {
            throw new RuntimeException("시간표 JSON 파싱 실패", e);
        }
    }
}
