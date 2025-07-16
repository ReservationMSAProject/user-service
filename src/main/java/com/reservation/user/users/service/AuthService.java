package com.reservation.user.users.service;

import com.reservation.user.jwt.JwtProvider;
import com.reservation.user.jwt.JwtTokenPair;
import com.reservation.user.jwt.JwtUtil;
import com.reservation.user.users.dto.TokenResponse;
import com.reservation.user.users.dto.UserInfoDTO;
import com.reservation.user.users.dto.UserLoginRequestDTO;
import com.reservation.user.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtUtil jwtUtil;
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public TokenResponse login(UserLoginRequestDTO request) {

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            UserInfoDTO userInfo = userService.findByUserEmailToken(request.getEmail());
            JwtTokenPair tokenPair = jwtProvider.generateTokenPair(authentication, userInfo);

            return TokenResponse.builder()
                    .accessToken(tokenPair.getAccessToken())
                    .refreshToken(tokenPair.getRefreshToken())
                    .tokenType(tokenPair.getTokenType())
                    .expiresIn(tokenPair.getExpiresIn())
                    .userInfo(userInfo)
                    .build();

        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("이메일 또는 비밀번호가 올바르지 않습니다.");
        } catch (Exception e) {
            throw new RuntimeException("로그인 처리 중 오류가 발생했습니다.");
        }
    }
}
