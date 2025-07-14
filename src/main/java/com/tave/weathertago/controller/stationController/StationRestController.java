package com.tave.weathertago.controller.stationController;

import com.tave.weathertago.apiPayload.ApiResponse;

import com.tave.weathertago.dto.station.StationResponseDTO;
import com.tave.weathertago.infrastructure.csv.StationCsvImporter;
import com.tave.weathertago.service.station.StationQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "Station", description = "지하철역 정보 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/station")
public class StationRestController {


    private final StationQueryService stationQueryService;

    private final StationCsvImporter stationCsvImporter; // CsvImporter로 변경

    @PostMapping("/initialize")
    public ApiResponse<String> initializeStations() {
        try (
                InputStream inputStream = getClass().getClassLoader().getResourceAsStream("station.xlsx.csv");
                InputStream locationStream = getClass().getClassLoader().getResourceAsStream("station_location.csv");
                InputStream contactStream = getClass().getClassLoader().getResourceAsStream("stationInfo.csv")
        ) {
            if (inputStream == null) {
                throw new RuntimeException("station.xlsx.csv 파일을 classpath에서 찾을 수 없습니다.");
            }
            if (locationStream == null) {
                throw new RuntimeException("station_location.csv 파일을 classpath에서 찾을 수 없습니다.");
            }
            if (contactStream == null) {
                throw new RuntimeException("stationInfo.csv 파일을 classpath에서 찾을 수 없습니다.");
            }

            stationCsvImporter.importFromCsv(inputStream);              // 역 목록 저장
            stationCsvImporter.importFromLocationCsv(locationStream);   // 좌표 정보 저장
            stationCsvImporter.importFromContactCsv(contactStream);     // 전화번호 + 주소 정보 저장

            return ApiResponse.onSuccess("역 정보 + 좌표 + 연락처 초기화 완료");
        } catch (IOException e) {
            throw new RuntimeException("CSV 파일을 읽는 중 오류가 발생했습니다.", e);
        }
    }


    @GetMapping("/search")
    public ApiResponse<StationResponseDTO.JoinResultDTO> getStationById(
            @RequestParam("stationId") Long stationId,
            @RequestParam("time") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime queryTime
    ) {
        StationResponseDTO.JoinResultDTO result = stationQueryService.getStationById(stationId, queryTime);
        return ApiResponse.onSuccess(result);
    }

    @GetMapping("/info")
    public ApiResponse<List<StationResponseDTO.SimpleStationDTO>> getAllSimpleStations() {
        List<StationResponseDTO.SimpleStationDTO> result = stationQueryService.getAllSimpleStations();
        return ApiResponse.onSuccess(result);
    }

    @GetMapping("/detailInfo")
    public ApiResponse<List<StationResponseDTO.StationInfoDTO>> getAllStationsInfo() {
        List<StationResponseDTO.StationInfoDTO> result = stationQueryService.getAllStationsInfo();
        return ApiResponse.onSuccess(result);
    }

    @Operation(summary = "지하철 역 시간대별 상태 조회", description = "현재 시각부터 3일 뒤 00시까지의 날씨와 혼잡도 정보를 시간대별로 조회합니다.")
    @GetMapping("/status")
    public ApiResponse<StationResponseDTO.StationStatusResponseDTO> getStatus(
            @Parameter(description = "조회할 역의 ID")
            @RequestParam("stationId") Long stationId
    ) {
        return ApiResponse.onSuccess(
                stationQueryService.getStatus(stationId)
        );
    }

}
