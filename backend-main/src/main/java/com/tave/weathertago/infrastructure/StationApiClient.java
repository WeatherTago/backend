/*
package com.tave.weathertago.infrastructure;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tave.weathertago.dto.Station.StationDTO;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

@Component
public class StationApiClient {

    private static final String BASE_URL = "http://openapi.seoul.go.kr:8088";
    private static final String API_KEY = "416a715a4b666377333154616e764a";

    public List<StationDTO> fetchAllStations() {
        try {
            StringBuilder urlBuilder = new StringBuilder(BASE_URL);
            urlBuilder.append("/").append(URLEncoder.encode(API_KEY, "UTF-8"));
            urlBuilder.append("/json/SearchInfoBySubwayNameService/1/1000");


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

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(sb.toString());
            JsonNode rows = root.path("SearchInfoBySubwayNameService").path("row");

            List<StationDTO> stations = new ArrayList<>();

            for (JsonNode row : rows) {
                String name = row.path("STATION_NM").asText();
                String lineName = row.path("LINE_NUM").asText();
                String code = row.path("STATION_CD").asText();

                if (name == null || lineName == null || code == null) {
                    continue;
                }

                StationDTO dto = new StationDTO(
                        name,
                        lineName,
                        code,
                        null,
                        null
                );
                stations.add(dto);
            }

            return stations;

        } catch (IOException e) {
            throw new RuntimeException("역 목록 조회 실패", e);
        }
    }


    public String fetchSubwayStats(String date) {
        try {
            StringBuilder urlBuilder = new StringBuilder(BASE_URL);
            urlBuilder.append("/").append(URLEncoder.encode(API_KEY, "UTF-8"));
            urlBuilder.append("/").append("json");
            urlBuilder.append("/").append("SearchInfoBySubwayNameService");
            urlBuilder.append("/").append("1");
            urlBuilder.append("/").append("1000");
            urlBuilder.append("/").append(date);

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

            return sb.toString();

        } catch (IOException e) {
            throw new RuntimeException("서울 Open API 호출 실패", e);
        }
    }

    public String fetchStationCode(String stationName, String lineName) {
        try {
            StringBuilder urlBuilder = new StringBuilder(BASE_URL);
            urlBuilder.append("/").append(URLEncoder.encode(API_KEY, "UTF-8"));
            urlBuilder.append("/").append("json");
            urlBuilder.append("/").append("SearchInfoBySubwayNameService");
            urlBuilder.append("/").append("1");
            urlBuilder.append("/").append("5");
            urlBuilder.append("/").append(URLEncoder.encode(stationName, "UTF-8"));

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

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(sb.toString());
            JsonNode rows = root.path("SearchInfoBySubwayNameService").path("row");

            for (JsonNode row : rows) {
                String subwayName = row.path("STATION_NM").asText();
                String linevalue = row.path("LINE_NUM").asText();
                if (subwayName.equals(stationName) && (lineName == null || linevalue.equals(lineName))) {
                    return row.path("STATION_CD").asText();
                }
            }

            throw new RuntimeException("해당 역을 찾을 수 없습니다.");

        } catch (IOException e) {
            throw new RuntimeException("역 코드 조회 실패", e);
        }
    }
}
 */
