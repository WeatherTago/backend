package com.tave.weathertago.service.Station;

import com.tave.weathertago.converter.StationConverter;
import com.tave.weathertago.domain.Station;
import com.tave.weathertago.dto.Station.StationDTO;
import com.tave.weathertago.infrastructure.csv.StationCsvImporter;
import com.tave.weathertago.repository.StationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StationCommandServiceImpl implements StationCommandService {

    private final StationRepository stationRepository;
    private final StationCsvImporter stationCsvImporter;

    @Override
    @Transactional
    public void initializeStations() {
        // CSV 경로 지정
        String csvPath = "src/main/resources/stations.xlsx.csv";

        // CSV를 통해 초기화
        stationCsvImporter.importFromCsv(csvPath);
    }
}
