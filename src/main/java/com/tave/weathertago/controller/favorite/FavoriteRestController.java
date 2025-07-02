package com.tave.weathertago.controller.favorite;

import com.tave.weathertago.apiPayload.ApiResponse;
import com.tave.weathertago.dto.favorite.FavoriteRequestDTO;
import com.tave.weathertago.dto.favorite.FavoriteResponseDTO;
import com.tave.weathertago.dto.station.StationResponseDTO;
import com.tave.weathertago.service.favorite.FavoriteCommandService;
import com.tave.weathertago.service.favorite.FavoriteQueryService;
import com.tave.weathertago.service.station.StationQueryService;
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
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> addFavorite(@RequestBody FavoriteRequestDTO requestDTO){
        favoriteCommandService.addStationToFavorite(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.onSuccess(null)); //result는 없지만 성공 응답 하기 위함.
    }

    //즐겨찾기 삭제
    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> removeFavorite(@RequestBody FavoriteRequestDTO requestDTO){
        favoriteCommandService.removeStationFromFavorite(requestDTO);
        return ResponseEntity.ok(ApiResponse.onSuccess(null)); // 삭제 성공
    }

    //나의 즐겨찾기 목록 조회
    @GetMapping
    public ResponseEntity<ApiResponse<FavoriteResponseDTO.FavoriteResultDTO>> getMyFavorite() {
        FavoriteResponseDTO.FavoriteResultDTO favorite = favoriteQueryService.getMyFavorite();
        return ResponseEntity.ok(ApiResponse.onSuccess(favorite));
    }

    //즐겨찾기 내 특정 역 상세 조회
    @GetMapping("/detail")
    public ResponseEntity<ApiResponse<StationResponseDTO.JoinResultDTO>> getStationDetailByNameAndLine(
            @RequestParam("name") String stationName,
            @RequestParam("line") String stationLine,
            @RequestParam("time") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime time) {

        StationResponseDTO.JoinResultDTO stationDetail = stationQueryService.getStationByNameAndLine(stationName, stationLine, time);
        return ResponseEntity.ok(ApiResponse.onSuccess(stationDetail));
    }
}
