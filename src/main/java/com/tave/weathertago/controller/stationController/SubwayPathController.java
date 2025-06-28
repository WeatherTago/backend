package com.tave.weathertago.controller.stationController;

import com.tave.weathertago.apiPayload.code.status.ErrorStatus;
import com.tave.weathertago.apiPayload.exception.handler.StationHandler;
import com.tave.weathertago.converter.SubwayPathConverter;
import com.tave.weathertago.domain.Station;
import com.tave.weathertago.dto.Station.SubwayPathDTO;
import com.tave.weathertago.dto.Station.SubwayPathResponseDTO;
import com.tave.weathertago.infrastructure.SubwayOpenApiClient;
import com.tave.weathertago.repository.StationRepository;
import com.tave.weathertago.service.Station.SubwayPathService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/subway")
public class SubwayPathController {

    private final SubwayPathService subwayPathService;

    @GetMapping("/path")
    public ResponseEntity<SubwayPathDTO> getPath(
            @RequestParam String start,
            @RequestParam String end) {

        SubwayPathDTO result = subwayPathService.findPath(start, end);
        return ResponseEntity.ok(result);
    }
}
