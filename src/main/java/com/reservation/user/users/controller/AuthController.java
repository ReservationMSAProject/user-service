package com.reservation.user.users.controller;

import com.reservation.user.jwt.JwtProvider;
import com.reservation.user.jwt.JwtTokenPair;
import com.reservation.user.jwt.JwtUtil;
import com.reservation.user.users.dto.*;
import com.reservation.user.users.service.AuthService;
import com.reservation.user.users.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.Duration;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/auth/api/v1")
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final AuthService authService;


    // 로그인
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody @Valid UserLoginRequestDTO request,
                                               HttpServletResponse response) {
        log.info("로그인 요청: {}", request.getEmail());

        TokenResponse tokenResponse = authService.login(request);

        // ResponseCookie 사용
        ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", tokenResponse.getAccessToken())
                .httpOnly(true)
//                .secure(true)
                .maxAge(Duration.ofSeconds(tokenResponse.getExpiresIn()))
                .path("/")
//                .sameSite("Strict")  // 문자열로 설정
                .sameSite("Lax")
                .build();

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", tokenResponse.getRefreshToken())
                .httpOnly(true)
//                .secure(true)
                .maxAge(Duration.ofDays(30))
                .path("/")
                .sameSite("Strict")
                .build();

        // Set-Cookie 헤더에 추가
        response.addHeader("Set-Cookie", accessTokenCookie.toString());
        response.addHeader("Set-Cookie", refreshTokenCookie.toString());

        // 응답에서는 토큰 제거
        TokenResponse clientResponse = TokenResponse.builder()
                .tokenType(tokenResponse.getTokenType())
                .expiresIn(tokenResponse.getExpiresIn())
                .userInfo(tokenResponse.getUserInfo())
                .build();

        log.info("로그인 성공: {}", request.getEmail());
        return ResponseEntity.ok(clientResponse);
    }



    // OAuth2 로그인 성공 후 사용자 정보 조회
    @GetMapping("/oauth2/user")
    public ResponseEntity<UserInfoDTO> getOAuth2User(HttpServletRequest request) {
        UserInfoDTO userInfo = jwtUtil.getCurrentUserInfo(request);
        return ResponseEntity.ok(userInfo);
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response) {
        // Access Token 쿠키 삭제
        Cookie accessTokenCookie = new Cookie("accessToken", null);
        accessTokenCookie.setMaxAge(0);
        accessTokenCookie.setPath("/");
        response.addCookie(accessTokenCookie);

        // Refresh Token 쿠키 삭제
        Cookie refreshTokenCookie = new Cookie("refreshToken", null);
        refreshTokenCookie.setMaxAge(0);
        refreshTokenCookie.setPath("/");
        response.addCookie(refreshTokenCookie);

        return ResponseEntity.ok("로그아웃 성공");
    }

    // 토큰 검증
    @PostMapping("/token/valid")
    public ResponseEntity<String> validateToken(HttpServletRequest request) {

       UserInfoDTO userInfo = jwtUtil.getCurrentUserInfo(request);
        return ResponseEntity.ok("토큰이 유효합니다. 사용자 정보: " + userInfo);
    }


    // 회원가입
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody @Valid UserDto userDto) {
        log.info("User registration request received: {}", userDto);

        userService.saveUser(userDto);
        return ResponseEntity.ok("회원가입 성공");
    }

    // 이메일 검증
    @PostMapping("/email/valid")
    public ResponseEntity<String> validateEmail(@RequestBody @Valid UserEmailRequestDTO email) {
        return userService.isEmailExists(email)
                ? ResponseEntity.badRequest().body("이미 사용 중인 이메일입니다.")
                : ResponseEntity.ok("사용 가능한 이메일입니다.");
    }
    // 전화번호 검증
    @PostMapping("/phone/valid")
    public ResponseEntity<String> validatePhone(@RequestBody @Valid UserPhoneRequestDTO phoneNumber) {
        return userService.isPhoneNumberExists(phoneNumber)
                ? ResponseEntity.badRequest().body("이미 사용 중인 전화번호 입니다.")
                : ResponseEntity.ok("사용 가능한 전화번호 입니다.");
    }

}
