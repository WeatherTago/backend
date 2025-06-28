package com.tave.weathertago.infrastructure.csv;

import com.tave.weathertago.apiPayload.code.status.ErrorStatus;
import com.tave.weathertago.apiPayload.exception.handler.StationHandler;
import com.tave.weathertago.domain.Station;
import com.tave.weathertago.repository.StationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StationCsvImporter {

    private final StationRepository stationRepository;

    public void importFromCsv(InputStream inputStream) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            boolean isFirst = true;
            while ((line = reader.readLine()) != null) {
                if (isFirst) {
                    isFirst = false;
                    continue;
                }

                String[] fields = line.split(",");
                if (fields.length < 3) continue; // 필드 부족한 경우 skip

                String code = fields[0].trim();     // 전철역코드
                String name = fields[1].trim();     // 전철역명
                String lineName = fields[2].trim(); // 호선

                // 저장
                Station station = Station.builder()
                        .stationCode(code)
                        .name(name)
                        .line(lineName)
                        .build();

                // 중복 방지
                if (!stationRepository.existsByNameAndLine(name, lineName)) stationRepository.save(station);
            }
        } catch (IOException e) {
            throw new RuntimeException("CSV 파일 읽기 실패", e);
        }
    }

    /**
     * 좌표 CSV(호선,역명,위도,경도) → 기존 역 정보에 좌표 보완 (무조건 덮어쓰기)
     */
    @Transactional
    public void importFromLocationCsv(InputStream locationStream) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(locationStream, StandardCharsets.UTF_8))) {
            String line;
            boolean isFirst = true;

            while ((line = reader.readLine()) != null) {
                if (isFirst) {
                    isFirst = false;
                    continue;
                }

                String[] fields = line.split("\\s*,\\s*");
                if (fields.length < 4) continue;

                String lineName = fields[0].trim();
                String name = fields[1].trim();
                Double latitude = tryParseDouble(fields[2].trim());
                Double longitude = tryParseDouble(fields[3].trim());

                if (latitude == null || longitude == null) {
                    System.out.printf("❌ 좌표 파싱 실패: %s (%s)%n", name, lineName);
                    continue;
                }

                Optional<Station> optionalStation = stationRepository.findByNameAndLine(name, lineName);
                if (optionalStation.isPresent()) {
                    Station station = optionalStation.get();
                    station.updateLocation(latitude, longitude); // 무조건 덮어쓰기
                    stationRepository.save(station);
                    System.out.printf("📍 좌표 강제 업데이트: %s (%s) → %.6f, %.6f%n", name, lineName, latitude, longitude);
                } else {
                    System.out.printf("🔍 일치하는 역 없음: %s (%s)%n", name, lineName);
                }
            }

        } catch (IOException e) {
            throw new StationHandler(ErrorStatus.FILE_READ_ERROR);
        }
    }

    private Double tryParseDouble(String value) {
        try {
            return (value == null || value.isBlank()) ? null : Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}

