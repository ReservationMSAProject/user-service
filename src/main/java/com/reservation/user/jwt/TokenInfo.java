package com.reservation.user.jwt;


public record TokenInfo(
        String grantType,
        String accessToken,
        String refreshToken,
        Long expiresIn
) {}
