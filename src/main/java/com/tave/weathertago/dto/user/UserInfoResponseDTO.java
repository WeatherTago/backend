package com.tave.weathertago.dto.user;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserInfoResponseDTO {
    private String nickname;
    private String email;
}