package com.tave.weathertago.service.Station;

import com.tave.weathertago.infrastructure.csv.StationCsvImporter;
import com.tave.weathertago.repository.StationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class StationCommandServiceImpl implements StationCommandService {

    private final StationRepository stationRepository;
    private final StationCsvImporter stationCsvImporter;

    @Override
    @Transactional
    public void initializeStations() {
        // classpath에서 리소스 InputStream으로 읽기
        try (InputStream inputStream = new ClassPathResource("stations.xlsx.csv").getInputStream()) {
            stationCsvImporter.importFromCsv(inputStream);
        } catch (IOException e) {
            throw new RuntimeException("CSV 파일을 읽는 중 오류가 발생했습니다.", e);
        }

        // 좌표 정보 import
        try (InputStream locationStream = new ClassPathResource("station_location.csv").getInputStream()) {
            stationCsvImporter.importFromLocationCsv(locationStream); // <- 메서드 시그니처 수정 필요
        } catch (IOException e) {
            throw new RuntimeException("좌표 CSV 파일을 읽는 중 오류가 발생했습니다.", e);
        }
    }
}
