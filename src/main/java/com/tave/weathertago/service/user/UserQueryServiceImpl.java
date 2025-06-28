package com.tave.weathertago.service.user;

import com.tave.weathertago.apiPayload.code.status.ErrorStatus;
import com.tave.weathertago.apiPayload.exception.GeneralException;
import com.tave.weathertago.apiPayload.exception.handler.UserHandler;
import com.tave.weathertago.converter.UserConverter;
import com.tave.weathertago.domain.User;
import com.tave.weathertago.dto.user.UserInfoResponseDTO;
import com.tave.weathertago.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserQueryServiceImpl implements UserQueryService{

    private final UserRepository userRepository;

    @Override
    public UserInfoResponseDTO getMyInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String kakaoId =  authentication.getName();

        User user = userRepository.findByKakaoId(kakaoId)
                .orElseThrow(()->new UserHandler(ErrorStatus.USER_NOT_FOUND));

        return UserConverter.toUserInfoResponse(user);
    }
}
