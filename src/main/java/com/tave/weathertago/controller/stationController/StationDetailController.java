package com.tave.weathertago.controller.stationController;

import com.tave.weathertago.apiPayload.ApiResponse;
import com.tave.weathertago.dto.StationDetail.StationDetailResponseDTO;
import com.tave.weathertago.service.StationDetail.StationDetailQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/station")
public class StationDetailController {

    private final StationDetailQueryService stationDetailQueryService;

    @GetMapping("/details")
    public ApiResponse<StationDetailResponseDTO.Response> getDetails(
            @RequestParam String name,
            @RequestParam String line
    ) {
        StationDetailResponseDTO.Response response = stationDetailQueryService.getDetail(name, line);
        return ApiResponse.onSuccess(response);
    }
}
