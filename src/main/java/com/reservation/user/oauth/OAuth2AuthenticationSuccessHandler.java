package com.reservation.user.oauth;

import com.reservation.user.jwt.JwtProvider;
import com.reservation.user.jwt.JwtTokenPair;
import com.reservation.user.users.dto.UserInfoDTO;
import com.reservation.user.users.service.UserService;
import jakarta.persistence.EntityNotFoundException;
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
                                        Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");

        try {
            // 기존 사용자 조회
            UserInfoDTO userInfo = userService.findByUserEmailToken(email);
            generateTokensAndRedirect(request, response, authentication, userInfo);

        } catch (EntityNotFoundException e) {
            // 사용자가 없으면 자동 회원가입 처리
            UserInfoDTO newUser = createNewUserFromOAuth2(oAuth2User);
            generateTokensAndRedirect(request, response, authentication, newUser);
        }
    }

    private void generateTokensAndRedirect(HttpServletRequest request, HttpServletResponse response,
                                           Authentication authentication, UserInfoDTO userInfo) throws IOException {
        // JWT 토큰 생성
        JwtTokenPair tokenPair = jwtProvider.generateTokenPair(authentication, userInfo);

        // 쿠키 설정
        setAuthCookies(response, tokenPair);

        // 세션 무효화
        request.getSession().invalidate();

        // 프론트엔드 리디렉션
        response.sendRedirect("http://localhost:5173/profile");
    }

    private UserInfoDTO createNewUserFromOAuth2(OAuth2User oAuth2User) {
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        // 새 사용자 생성 로직
        return userService.createOAuth2User(email, name);
    }

    private void setAuthCookies(HttpServletResponse response, JwtTokenPair tokenPair) {
        ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", tokenPair.getAccessToken())
                .httpOnly(true)
//                .secure(true)
                .maxAge(Duration.ofSeconds(tokenPair.getExpiresIn()))
                .path("/")
                .sameSite("Strict")
                .build();

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", tokenPair.getRefreshToken())
                .httpOnly(true)
//                .secure(true)
                .maxAge(Duration.ofDays(30))
                .path("/")
                .sameSite("Strict")
                .build();

        response.addHeader("Set-Cookie", accessTokenCookie.toString());
        response.addHeader("Set-Cookie", refreshTokenCookie.toString());
    }
}
