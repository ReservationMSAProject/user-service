package com.reservation.user.jwt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JwtTokenPair {

    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private Long expiresIn;

}
