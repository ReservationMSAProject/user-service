package com.reservation.user.users.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link com.reservation.user.users.domain.UserEntity}
 */
@Value
public class UserLoginRequestDTO implements Serializable {

    @NotBlank(message = "아이디 또는 이메일은 비워둘 수 없습니다.")
    String email;

    @NotBlank(message = "비밀번호는 비워둘 수 없습니다.")
    String password;
}