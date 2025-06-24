package com.tave.weathertago.converter;

import com.tave.weathertago.domain.User;
import com.tave.weathertago.dto.user.UserInfoResponseDTO;

public class UserConverter {

    public static UserInfoResponseDTO toUserInfoResponse(User user) {
        return UserInfoResponseDTO.builder()
                .nickname(user.getNickname())
                .email(user.getEmail())
                .build();
    }
}