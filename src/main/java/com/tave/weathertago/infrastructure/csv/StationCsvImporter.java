package com.tave.weathertago.infrastructure.csv;

import com.tave.weathertago.apiPayload.code.status.ErrorStatus;
import com.tave.weathertago.apiPayload.exception.handler.StationHandler;
import com.tave.weathertago.domain.Station;
import com.tave.weathertago.repository.StationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class StationCsvImporter {

    private final StationRepository stationRepository;

    /**
     * 기본 정보만 있는 CSV (역 코드, 이름, 호선 등) → 중복 없을 때만 저장
     */
    public void importFromCsv(String csvPath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(csvPath))) {
            String line;
            boolean isFirst = true;

            while ((line = reader.readLine()) != null) {
                if (isFirst) {
                    isFirst = false;
                    continue;
                }

                String[] fields = line.split("\\s*,\\s*"); // 통일된 split
                if (fields.length < 3) continue;

                String code = fields[0].trim();
                String name = fields[1].trim();
                String lineName = fields[2].trim();

                if (!stationRepository.existsByNameAndLine(name, lineName)) {
                    Station station = Station.builder()
                            .stationCode(code)
                            .name(name)
                            .line(lineName)
                            .build();

                    stationRepository.save(station);
                    System.out.printf("✅ 기본 정보 저장됨: %s (%s)%n", name, lineName);
                }
            }
        } catch (IOException e) {
            throw new StationHandler(ErrorStatus.FILE_READ_ERROR);
        }
    }

    /**
     * 좌표 CSV(호선,역명,위도,경도) → 기존 역 정보에 좌표 보완 (무조건 덮어쓰기)
     */
    public void importFromLocationCsv(String csvPath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(csvPath))) {
            String line;
            boolean isFirst = true;

            while ((line = reader.readLine()) != null) {
                if (isFirst) {
                    isFirst = false;
                    continue;
                }

                String[] fields = line.split("\\s*,\\s*"); // 통일된 split
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

                    // 무조건 좌표 덮어쓰기
                    station.updateLocation(latitude, longitude);
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




