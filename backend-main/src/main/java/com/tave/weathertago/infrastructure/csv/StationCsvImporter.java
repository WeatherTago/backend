package com.tave.weathertago.infrastructure.csv;

import com.tave.weathertago.domain.Station;
import com.tave.weathertago.repository.StationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class StationCsvImporter {

    private final StationRepository stationRepository;

    public void importFromCsv(String csvPath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(csvPath))) {
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
}

