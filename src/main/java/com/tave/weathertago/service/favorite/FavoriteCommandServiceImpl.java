package com.tave.weathertago.service.favorite;

import com.tave.weathertago.apiPayload.code.status.ErrorStatus;
import com.tave.weathertago.apiPayload.exception.handler.FavoriteHandler;
import com.tave.weathertago.apiPayload.exception.handler.StationHandler;
import com.tave.weathertago.apiPayload.exception.handler.UserHandler;
import com.tave.weathertago.domain.Favorite;
import com.tave.weathertago.domain.Station;
import com.tave.weathertago.domain.User;
import com.tave.weathertago.dto.favorite.FavoriteRequestDTO;
import com.tave.weathertago.repository.FavoriteRepository;
import com.tave.weathertago.repository.StationRepository;
import com.tave.weathertago.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class FavoriteCommandServiceImpl implements FavoriteCommandService {

    private final FavoriteRepository favoriteRepository;
    private final StationRepository stationRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void addStationToFavorite(FavoriteRequestDTO requestDTO) {
        // 1. 현재 로그인한 사용자 kakaoId 추출
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String kakaoId =  authentication.getName();

        // 2. DB에서 영속 상태의 User 조회 (Transient 오류 방지)
        User user = userRepository.findByKakaoId(kakaoId)
                .orElseThrow(() -> new UserHandler(ErrorStatus.USER_NOT_FOUND));

        // 3. 즐겨찾기(Favorite) 엔티티 조회 (없으면 새로 생성해서 저장)
        Favorite favorite = favoriteRepository.findByUser_KakaoId(kakaoId)
                .orElseGet(() -> favoriteRepository.save(
                        Favorite.builder()
                                .user(user)
                                .stations(new ArrayList<>())
                                .build()
                ));

        // 4. 요청으로부터 역 정보 조회
        Station station = stationRepository.findByNameAndLine(
                requestDTO.getStationName(),
                requestDTO.getStationLine()
        ).orElseThrow(() -> new StationHandler(ErrorStatus.STATION_NAME_NOT_FOUND));

        // 5. 중복 여부 확인 후 역 추가
        if (!favorite.getStations().contains(station)) {
            favorite.getStations().add(station);
        }
    }



    @Override
    @Transactional
    public void removeStationFromFavorite(FavoriteRequestDTO requestDTO) {
        String kakaoId = getCurrentKakaoId();

        Favorite favorite = favoriteRepository.findByUser_KakaoId(kakaoId)
                .orElseThrow(() -> new FavoriteHandler(ErrorStatus.FAVORITE_NOT_FOUND));

        Station station = stationRepository.findByNameAndLine(
                requestDTO.getStationName(),
                requestDTO.getStationLine()
        ).orElseThrow(() -> new StationHandler(ErrorStatus.STATION_NAME_NOT_FOUND));

        favorite.getStations().remove(station);
    }


    private String getCurrentKakaoId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();  // kakaoId
    }
}
