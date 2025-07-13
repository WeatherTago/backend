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
import java.util.List;
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
                if (fields.length < 4) continue;

                String code = fields[0].trim();
                String name = fields[1].trim();
                String rawLineNumber = fields[2].trim();    // 예: "1"
                String directionCode = fields[3].trim();    // 예: "0", "1", "2", "3"

                String lineName = rawLineNumber + "호선";
                String direction = mapDirection(directionCode);

                Station station = Station.builder()
                        .stationCode(code)
                        .name(name)
                        .line(lineName)
                        .direction(direction)
                        .build();

                if (!stationRepository.existsByNameAndLineAndDirection(name, lineName, direction)) {
                    stationRepository.save(station);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("CSV 파일 읽기 실패", e);
        }
    }

    private String mapDirection(String code) {
        return switch (code) {
            case "0" -> "상행";
            case "1" -> "하행";
            case "2" -> "외선";
            case "3" -> "내선";
            default -> "미정";
        };
    }

    /**
     * 좌표 CSV(호선,역명,위도,경도) → 기존 역 정보에 좌표 보완 (name + line 기준으로 모든 direction 적용)
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

                List<Station> matchingStations = stationRepository.findAllByNameAndLine(name, lineName);
                if (!matchingStations.isEmpty()) {
                    for (Station station : matchingStations) {
                        station.updateLocation(latitude, longitude); // 무조건 덮어쓰기
                        stationRepository.save(station);
                        System.out.printf("📍 좌표 강제 업데이트: %s (%s, %s) → %.6f, %.6f%n",
                                station.getName(), station.getLine(), station.getDirection(), latitude, longitude);
                    }
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

    /**
     * 전화번호/주소 CSV(호선,역명,전화번호,도로명주소) → 기존 역 정보 보완 (name + line 기준으로 모든 direction 적용)
     */
    @Transactional
    public void importFromContactCsv(InputStream contactStream) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(contactStream, StandardCharsets.UTF_8))) {
            String line;
            boolean isFirst = true;

            while ((line = reader.readLine()) != null) {
                if (isFirst) {
                    isFirst = false;
                    continue;
                }

                String[] fields = line.split("\\s*,\\s*");
                if (fields.length < 4) continue;

                String rawLine = fields[0].trim();           // "1"
                String name = fields[1].trim();              // "서울역"
                String phone = fields[2].trim();             // "02-123-4567"
                String address = fields[3].trim();           // "서울특별시 중구 세종대로 100"

                String lineName = rawLine + "호선";

                List<Station> matchingStations = stationRepository.findAllByNameAndLine(name, lineName);
                if (!matchingStations.isEmpty()) {
                    for (Station station : matchingStations) {
                        station.updateContact(phone, address); // 새 메서드로 연락처/주소 설정
                        stationRepository.save(station);
                        System.out.printf("정보 업데이트: %s (%s, %s) → %s / %s%n",
                                station.getName(), station.getLine(), station.getDirection(), phone, address);
                    }
                } else {
                    System.out.printf("일치하는 역 없음: %s (%s)%n", name, lineName);
                }
            }

        } catch (IOException e) {
            throw new StationHandler(ErrorStatus.FILE_READ_ERROR);
        }
    }

}

