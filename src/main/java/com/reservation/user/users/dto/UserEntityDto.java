package com.reservation.user.users.dto;

import com.reservation.user.users.enums.Roles;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link com.reservation.user.users.domain.UserEntity}
 */
public record UserEntityDto(String name, String email, String password, String phoneNumber, Roles roles, String address,
                            LocalDateTime createdAt) implements Serializable {
}