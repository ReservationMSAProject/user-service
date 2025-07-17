package com.reservation.user.users.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserPhoneRequestDTO(
        @NotBlank(message = "휴대폰 번호는 비워둘 수 없습니다.")
        @Pattern(regexp = "^01[0-9]{8,9}$", message = "유효한 한국 휴대폰 번호 형식이 아닙니다. (예: 01012345678)")
        @Size(min = 10, max = 11, message = "휴대폰 번호 길이는 10~11자여야 합니다.")
        String phoneNumber
) {}