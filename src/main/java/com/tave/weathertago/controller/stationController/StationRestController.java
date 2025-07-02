package com.tave.weathertago.controller.stationController;

import com.tave.weathertago.apiPayload.ApiResponse;
import com.tave.weathertago.dto.station.StationResponseDTO;
import com.tave.weathertago.infrastructure.csv.StationCsvImporter;
import com.tave.weathertago.service.station.StationQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/station")
public class StationRestController {

    private final StationCommandService stationCommandService;

    private final StationQueryService stationQueryService;

    private final StationCsvImporter stationCsvImporter; // CsvImporter로 변경

    @PostMapping("/initialize")
    public ApiResponse<String> initializeStations() {
        try (
                InputStream inputStream = getClass().getClassLoader().getResourceAsStream("station.xlsx.csv");
                InputStream locationStream = getClass().getClassLoader().getResourceAsStream("station_location.csv")
        ) {
            if (inputStream == null) {
                throw new RuntimeException("station.xlsx.csv 파일을 classpath에서 찾을 수 없습니다.");
            }

            if (locationStream == null) {
                throw new RuntimeException("station_location.csv 파일을 classpath에서 찾을 수 없습니다.");
            }

            stationCsvImporter.importFromCsv(inputStream);              // 역 목록 저장
            stationCsvImporter.importFromLocationCsv(locationStream);   // ⬅ 좌표 정보 저장

            return ApiResponse.onSuccess("역 정보 + 좌표 초기화 완료");
        } catch (IOException e) {
            throw new RuntimeException("CSV 파일을 읽는 중 오류가 발생했습니다.", e);
        }
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
