package com.tave.weathertago.service.favorite;

import com.tave.weathertago.dto.favorite.FavoriteResponseDTO;

import java.util.List;

public interface FavoriteQueryService {
    FavoriteResponseDTO.FavoriteResultDTO getMyFavorite();
}