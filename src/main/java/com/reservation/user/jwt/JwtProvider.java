package com.reservation.user.jwt;

import com.reservation.user.users.dto.UserInfoDTO;
import com.reservation.user.users.enums.Roles;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.io.Decoders;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Component
@Slf4j
public class JwtProvider {

    public final SecretKey secretKey;
    private final long accessExpire;
    private final long refreshExpire;

    public JwtProvider(@Value("${jwt.secret}") String secretKey,
                       @Value("${jwt.expire.access}") long accessExpire,
                       @Value("${jwt.expire.refresh}") long refreshExpire) {
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
        this.accessExpire = accessExpire;
        this.refreshExpire = refreshExpire;
    }

    // 토큰 생성 (Access Token)
    public String generateAccessToken(Authentication authentication, UserInfoDTO userInfoDTO) {
        return generateToken(authentication, userInfoDTO, accessExpire, "ACCESS");
    }

    // 토큰 생성 (Refresh Token)
    public String generateRefreshToken(Authentication authentication, UserInfoDTO userInfoDTO) {
        return generateToken(authentication, userInfoDTO, refreshExpire, "REFRESH");
    }

    // 토큰 생성 (Access Token, Refresh Token 공통)
    public JwtTokenPair generateTokenPair(Authentication authentication, UserInfoDTO userInfoDTO) {
        String accessToken = generateAccessToken(authentication, userInfoDTO);
        String refreshToken = generateRefreshToken(authentication, userInfoDTO);

        return JwtTokenPair.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(accessExpire)
                .build();
    }

    // 토큰 생성
    private String generateToken(Authentication authentication,
                                UserInfoDTO userInfoDTO,
                                long expireTime, String tokenType) {

        String authorities = authentication.getAuthorities().stream()
                .map(grant -> grant.getAuthority())
                .collect(Collectors.joining(","));

        long now = System.currentTimeMillis();
        Date expiration = new Date(now + expireTime);
        return Jwts.builder()
                .subject(userInfoDTO.getEmail())
                .setIssuedAt(new Date(now))
                .setExpiration(expiration)
                .claim("id", userInfoDTO.getId())
                .claim("name", userInfoDTO.getName())
                .claim("email", userInfoDTO.getEmail())
                .claim("role", userInfoDTO.getRole())
                .claim("auth", authorities)
                .claim(("tokenType"), tokenType)
                .signWith(secretKey, Jwts.SIG.HS256)
                .compact();
    }


    // authentication 객체 생성
    public Authentication getAuthentication(String token) {
        Claims claims = getClaims(token);

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get("auth").toString().split(","))
                        .map(auth -> new SimpleGrantedAuthority(auth))
                        .toList();

        UserDetails principal = User.builder()
                .username(claims.get("email").toString())
                .password("")
                .authorities(authorities)
                .build();
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }



    // 토큰에서 유저 정보 추출
    public UserInfoDTO getUserInfo(String token) {
        Claims claims = getClaims(token);

        return UserInfoDTO.builder()
                .id(claims.get("id", Long.class))
                .name(claims.get("name", String.class))
                .email(claims.get("email", String.class))
                .role(Roles.valueOf(claims.get("role", String.class)))
                .build();
    }

    // 토큰 검증
    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.error("잘못된 JWT 서명입니다.");
        } catch (SignatureException e) {  // 추가
            log.error("JWT 서명이 일치하지 않습니다: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.error("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.error("JWT 토큰이 잘못되었습니다.");
        }
        return false;
    }

    // 페이로드 에서 인증 정보 추출
    private Claims getClaims(String token) {
       return Jwts.parser()
               .verifyWith(secretKey)
               .build()
               .parseSignedClaims(token)
               .getPayload();
    }
}
