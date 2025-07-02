package com.tave.weathertago.service.favorite;

import com.tave.weathertago.apiPayload.code.status.ErrorStatus;
import com.tave.weathertago.apiPayload.exception.handler.FavoriteHandler;
import com.tave.weathertago.converter.FavoriteConverter;
import com.tave.weathertago.domain.Favorite;
import com.tave.weathertago.dto.favorite.FavoriteResponseDTO;
import com.tave.weathertago.repository.FavoriteRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FavoriteQueryServiceImpl implements FavoriteQueryService {

    private final FavoriteRepository favoriteRepository;

    @Override
    @Transactional
    public FavoriteResponseDTO.FavoriteResultDTO getMyFavorite() {
        String kakaoId = getCurrentKakaoId();

        //즐겨찾기 가져오기
        Favorite favorite = favoriteRepository.findByUser_KakaoId(kakaoId)
                .orElseThrow(() -> new FavoriteHandler(ErrorStatus.FAVORITE_NOT_FOUND));

        return FavoriteConverter.toFavoriteResultDTO(favorite);
    }

    private String getCurrentKakaoId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();  // Jwt에서 추출된 kakaoId
    }
}
