package com.reservation.user.oauth;

import com.reservation.user.jwt.JwtProvider;
import com.reservation.user.jwt.JwtTokenPair;
import com.reservation.user.users.dto.UserInfoDTO;
import com.reservation.user.users.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;
    private final UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException, IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        
        // 사용자 정보 조회
        UserInfoDTO userInfo = userService.findByUserEmailToken(email);
        
        // JWT 토큰 생성
        JwtTokenPair tokenPair = jwtProvider.generateTokenPair(authentication, userInfo);
        
        // 쿠키 설정
        setAuthCookies(response, tokenPair);
        
        // 프론트엔드 리디렉션
        response.sendRedirect("http://localhost:5173/oauth2/redirect?success=true");
    }

    private void setAuthCookies(HttpServletResponse response, JwtTokenPair tokenPair) {
        ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", tokenPair.getAccessToken())
                .httpOnly(true)
                .secure(true)
                .maxAge(Duration.ofSeconds(tokenPair.getExpiresIn()))
                .path("/")
                .sameSite("Strict")
                .build();

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", tokenPair.getRefreshToken())
                .httpOnly(true)
                .secure(true)
                .maxAge(Duration.ofDays(30))
                .path("/")
                .sameSite("Strict")
                .build();

        response.addHeader("Set-Cookie", accessTokenCookie.toString());
        response.addHeader("Set-Cookie", refreshTokenCookie.toString());
    }
}
