package com.reservation.user.users.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserEmailRequestDTO(
        @NotBlank(message = "이메일은 비워둘 수 없습니다.")
        @Email(message = "유효한 이메일 형식이 아닙니다.")
        @Size(max = 254, message = "이메일 길이가 너무 깁니다. 최대 254자까지 허용됩니다.")
        String email
) {}