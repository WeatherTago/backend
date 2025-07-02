package com.tave.weathertago.dto.favorite;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class FavoriteResponseDTO {

    @Getter
    @Builder
    @AllArgsConstructor
    public static class FavoriteResultDTO{
        private Long favoriteId;
        private List<StationDTO> stations;

        @Getter
        @Builder
        @AllArgsConstructor
        @NoArgsConstructor
        public static class StationDTO {
            private String stationName;
            private String stationLine;
        }

    }

}
