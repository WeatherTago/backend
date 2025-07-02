package com.tave.weathertago.dto.favorite;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class FavoriteRequestDTO {
    private String stationName;
    private String stationLine;
}
