package com.reservation.user.users.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserDto(
    @NotBlank(message = "이름은 비워둘 수 없습니다.")
    @Size(min = 2, max = 50, message = "이름 길이는 2~50자 사이여야 합니다.")
    String name,

    @NotBlank(message = "이메일은 비워둘 수 없습니다.")
    @Email(message = "유효한 이메일 형식이 아닙니다.")
    @Size(max = 254, message = "이메일 길이가 너무 깁니다. 최대 254자까지 허용됩니다.")
    String email,

    @NotBlank(message = "비밀번호는 비워둘 수 없습니다.")
    @Size(min = 8, max = 100, message = "비밀번호 길이는 8~100자 사이여야 합니다.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$", message = "비밀번호는 영문자와 숫자를 포함해야 합니다.")
    String password,

    @NotBlank(message = "휴대폰 번호는 비워둘 수 없습니다.")
    @Pattern(regexp = "^01[0-9]{8,9}$", message = "유효한 한국 휴대폰 번호 형식이 아닙니다. (예: 01012345678)")
    @Size(min = 10, max = 11, message = "휴대폰 번호 길이는 10~11자여야 합니다.")
    String phoneNumber,

    @NotBlank(message = "주소는 비워둘 수 없습니다.")
    @Size(max = 255, message = "주소 길이가 너무 깁니다. 최대 255자까지 허용됩니다.")
    String address
) {}