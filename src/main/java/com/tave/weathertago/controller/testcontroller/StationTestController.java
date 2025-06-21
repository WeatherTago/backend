package com.tave.weathertago.controller.testcontroller;

import com.tave.weathertago.apiPayload.ApiResponse;
import com.tave.weathertago.converter.StationConverter;
import com.tave.weathertago.domain.Station;
import com.tave.weathertago.dto.Station.StationResponseDTO;
import com.tave.weathertago.service.Station.StationQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/station/test")
public class StationTestController {

    private final StationQueryService stationQueryService;

    @GetMapping("/all")
    public ApiResponse<List<StationResponseDTO.JoinResultDTO>> getAllStations() {
        List<Station> stations = stationQueryService.getAllStations(); // 이제 동작 가능
        List<StationResponseDTO.JoinResultDTO> result = stations.stream()
                .map(StationConverter::toJoinResultDTO)
                .toList();

        return ApiResponse.onSuccess(result);
    }
}