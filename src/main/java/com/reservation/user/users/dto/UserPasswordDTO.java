package com.reservation.user.users.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link com.reservation.user.users.domain.UserEntity}
 */
@Value
public class UserPasswordDTO implements Serializable {

    @NotBlank(message = "비밀번호는 비워둘 수 없습니다.")
    @Size(min = 8, max = 100, message = "비밀번호 길이는 8~100자 사이여야 합니다.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$", message = "비밀번호는 영문자와 숫자를 포함해야 합니다.")
    String password;

}