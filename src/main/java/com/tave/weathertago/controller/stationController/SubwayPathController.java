package com.tave.weathertago.controller.stationController;

import com.tave.weathertago.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import com.tave.weathertago.dto.station.SubwayPathDTO;
import com.tave.weathertago.service.station.SubwayPathQueryServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@Tag(name = "StationPath", description = "경로 찾기 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/subway")
public class SubwayPathController {

    private final SubwayPathQueryServiceImpl subwayPathQueryService;

    @Operation(summary = "경로 찾기" , description = "역과 역 사이의 경로를 조회합니다.")
    @GetMapping("/path")
    public ResponseEntity<ApiResponse<SubwayPathDTO>> getPath(
            @RequestParam("startStationId") Long startStationId,
            @RequestParam("endStationId") Long endStationId,
            @RequestParam("time") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime queryTime) {

        SubwayPathDTO result = subwayPathQueryService.findPath(startStationId, endStationId, queryTime);
        return ResponseEntity.ok(ApiResponse.onSuccess(result));
    }
}
