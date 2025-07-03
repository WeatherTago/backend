package com.tave.weathertago.service.favorite;

import com.tave.weathertago.dto.favorite.FavoriteRequestDTO;

public interface FavoriteCommandService {
    void addStationToFavorite(FavoriteRequestDTO requestDTO);
    void removeStationFromFavorite(FavoriteRequestDTO requestDTO);

}
