package com.reservation.user.users.service;

import com.reservation.user.users.domain.UserEntity;
import com.reservation.user.users.dto.UserDto;
import com.reservation.user.users.enums.Roles;
import com.reservation.user.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserEntity saveUser(UserDto user) {

        UserEntity userEntity = UserEntity.builder()
                .name(user.name())
                .email(user.email())
                .password(passwordEncoder.encode(user.password()))
                .phoneNumber(user.phoneNumber())
                .address(user.address())
                .role(Roles.USER)
                .isActive(true)
                .build();

        return userRepository.save(userEntity);
    }

}
