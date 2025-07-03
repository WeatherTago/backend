package com.tave.weathertago.converter;

import com.tave.weathertago.domain.Favorite;
import com.tave.weathertago.domain.Station;
import com.tave.weathertago.dto.favorite.FavoriteResponseDTO;
import com.tave.weathertago.dto.station.StationDTO;

import java.util.List;
import java.util.stream.Collectors;

public class FavoriteConverter {

    //FavoriteResultDTO안에 있는 StationDTO 변환
    public static FavoriteResponseDTO.FavoriteResultDTO.StationDTO toStationDTO(Station station){
        return FavoriteResponseDTO.FavoriteResultDTO.StationDTO.builder()
                .stationName(station.getName())
                .stationLine(station.getLine())
                .build();
    }


    //즐겨찾기에 속한 Station 엔티티 목록을 StationDTO 리스트로 변환하는 것이며,
    //map()을 이용해 하나씩 변환, collect()로 다시 리스트로 모아줌.
    public static FavoriteResponseDTO.FavoriteResultDTO toFavoriteResultDTO(Favorite favorite){
        List<FavoriteResponseDTO.FavoriteResultDTO.StationDTO> stationDTOList=favorite.getStations()
                .stream()
                .map(FavoriteConverter::toStationDTO)
                .collect(Collectors.toList());

        return FavoriteResponseDTO.FavoriteResultDTO.builder()
                .favoriteId(favorite.getFavoriteId())
                .stations(stationDTOList)
                .build();
    }

}
