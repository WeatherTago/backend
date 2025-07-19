package com.tave.weathertago.controller.favorite;

import com.tave.weathertago.apiPayload.ApiResponse;
import com.tave.weathertago.dto.favorite.FavoriteRequestDTO;
import com.tave.weathertago.dto.favorite.FavoriteResponseDTO;
import com.tave.weathertago.dto.station.StationResponseDTO;
import com.tave.weathertago.service.favorite.FavoriteCommandService;
import com.tave.weathertago.service.favorite.FavoriteQueryService;
import com.tave.weathertago.service.station.StationQueryService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/users/me/favorite")
@RequiredArgsConstructor
public class FavoriteRestController {

    private final FavoriteCommandService favoriteCommandService;
    private final FavoriteQueryService favoriteQueryService;
    private final StationQueryService stationQueryService;

    //즐겨찾기 등록
    @Operation(summary = "즐겨찾기 등록", description = "즐겨찾기 목록에 지하철역을 등록합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> addFavorite(@RequestBody FavoriteRequestDTO requestDTO){
        favoriteCommandService.addStationToFavorite(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.onSuccess(null)); //result는 없지만 성공 응답 하기 위함.
    }

    //즐겨찾기 삭제
    @Operation(summary = "즐겨찾기 삭제", description = "즐겨찾기 목록에 있는 지하철역을 삭제합니다.")
    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> removeFavorite(@RequestBody FavoriteRequestDTO requestDTO){
        favoriteCommandService.removeStationFromFavorite(requestDTO);
        return ResponseEntity.ok(ApiResponse.onSuccess(null)); // 삭제 성공
    }

    //나의 즐겨찾기 목록 조회
    @Operation(summary = "즐겨찾기 목록 조회", description = "즐겨찾기 목록에 있는 역들을 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<FavoriteResponseDTO.FavoriteResultDTO>> getMyFavorite() {
        FavoriteResponseDTO.FavoriteResultDTO favorite = favoriteQueryService.getMyFavorite();
        return ResponseEntity.ok(ApiResponse.onSuccess(favorite));
    }

    //즐겨찾기 내 특정 역 상세 조회
    @Operation(summary = "즐겨찾기 내 특정 역 상세 조회", description = "즐겨찾기 내 특정 역을 조회합니다.")
    @GetMapping("/detail")
    public ResponseEntity<ApiResponse<StationResponseDTO.JoinResultDTO>> getStationDetailById(
            @RequestParam("stationId") Long stationId,
            @RequestParam("time") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime time) {

        StationResponseDTO.JoinResultDTO stationDetail = stationQueryService.getStationById(stationId, time);
        return ResponseEntity.ok(ApiResponse.onSuccess(stationDetail));
    }
}
