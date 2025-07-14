package com.tave.weathertago.controller.user;

import com.tave.weathertago.apiPayload.ApiResponse;
import com.tave.weathertago.config.security.jwt.JwtTokenProvider;
import com.tave.weathertago.dto.user.UserInfoResponseDTO;
import com.tave.weathertago.service.user.UserCommandService;
import com.tave.weathertago.service.user.UserQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User", description = "유저 관련 API")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserRestController {

    private final UserQueryService userQueryService;
    private final UserCommandService userCommandService;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/me")
    @Operation(summary = "내 정보 조회", description = "로그인한 사용자의 닉네임과 이메일을 반환합니다.")
    public ApiResponse<UserInfoResponseDTO> getMyInfo() {
        return ApiResponse.onSuccess(userQueryService.getMyInfo());
    }

    @DeleteMapping("/withdraw")
    @Operation(summary = "회원 탈퇴", description = "회원 정보를 삭제하고 토큰을 무효화합니다.")
    public ApiResponse<Void> withdraw(HttpServletRequest request) {
        String accessToken = jwtTokenProvider.resolveToken(request);
        userCommandService.deleteUser(accessToken);
        return ApiResponse.onSuccess(null);
    }
}
