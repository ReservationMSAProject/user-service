package com.reservation.user.jwt;

import com.reservation.user.users.dto.UserInfoDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final JwtProvider jwtProvider;


    // 현재 사용자 정보 조회
    public UserInfoDTO getCurrentUserInfo(HttpServletRequest request) {
        String token = resolveToken(request);
        if(token != null && jwtProvider.validateToken(token)) {
            return jwtProvider.getUserInfo(token);
        }
        return null;
    }

    // 현재 사용자 ID 조회
    public Long getCurrentUserId(HttpServletRequest request) {
        UserInfoDTO userInfoDTO = getCurrentUserInfo(request);
        return userInfoDTO != null ? userInfoDTO.getId() : null;
    }

    // 헤더에서 토큰 추출
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

}
