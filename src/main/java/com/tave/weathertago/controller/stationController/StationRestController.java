package com.tave.weathertago.controller.stationController;

import com.tave.weathertago.apiPayload.ApiResponse;
import com.tave.weathertago.converter.StationConverter;
import com.tave.weathertago.domain.Station;
import com.tave.weathertago.dto.Station.StationResponseDTO;
import com.tave.weathertago.infrastructure.csv.StationCsvImporter;
import com.tave.weathertago.service.Station.StationCommandService;
import com.tave.weathertago.service.Station.StationQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/station")
public class StationRestController {

    private final StationCommandService stationCommandService;

    private final StationQueryService stationQueryService;

    private final StationCsvImporter stationCsvImporter; // CsvImporter로 변경

    @PostMapping("/initialize")
    public ApiResponse<String> initializeStations() {
        String path = Objects.requireNonNull(getClass().getClassLoader().getResource("station.xlsx.csv")).getPath();
        String locationPath = Objects.requireNonNull(getClass().getClassLoader().getResource("station_location.csv")).getPath();

        stationCsvImporter.importFromCsv(path);
        stationCsvImporter.importFromLocationCsv(locationPath); // ← 변경된 메서드 호출

        return ApiResponse.onSuccess("역 정보 초기화 완료");
    }

    @GetMapping("/search")
    public ApiResponse<List<StationResponseDTO.JoinResultDTO>> getStationsByName(@RequestParam("name") String name) {
        List<Station> stations = stationQueryService.getStationsByName(name);
        List<StationResponseDTO.JoinResultDTO> result = stations.stream()
                .map(StationConverter::toJoinResultDTO)
                .toList();

        return ApiResponse.onSuccess(result);
    }
}
