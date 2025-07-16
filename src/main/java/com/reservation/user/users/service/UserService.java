package com.reservation.user.users.service;

import com.reservation.user.users.domain.UserEntity;
import com.reservation.user.users.dto.UserDto;
import com.reservation.user.users.dto.UserEntityDto;
import com.reservation.user.users.dto.UserPasswordDTO;
import com.reservation.user.users.enums.Roles;
import com.reservation.user.users.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserEntity saveUser(UserDto user) {

        UserEntity userEntity = UserEntity.builder()
                .name(user.getName())
                .email(user.getEmail())
                .password(passwordEncoder.encode(user.getPassword()))
                .phoneNumber(user.getPhoneNumber())
                .address(user.getAddress())
                .role(Roles.USER)
                .isActive(true)
                .build();

        return userRepository.save(userEntity);
    }

    // 이메일로 사용자 조회
    @Transactional(readOnly = true)
    public UserEntityDto findByUserEmail(String email) {
        return userRepository.findByEmail(email)
                .map(user -> new UserEntityDto(
                        user.getName(),
                        user.getEmail(),
                        user.getPassword(),
                        user.getPhoneNumber(),
                        user.getAddress(),
                        user.getCreatedAt())
                )
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));
    }

    // 아이디로 사용자 조회
    @Transactional(readOnly = true)
    public UserEntityDto findByUserId(Long id) {
        return userRepository.findById(id)
                .map(user -> new UserEntityDto(
                        user.getName(),
                        user.getEmail(),
                        user.getPassword(),
                        user.getPhoneNumber(),
                        user.getAddress(),
                        user.getCreatedAt())
                )
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
    }

    // 모든 사용자 조회
    @Transactional(readOnly = true)
    public List<UserEntityDto> findAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> new UserEntityDto(
                        user.getName(),
                        user.getEmail(),
                        user.getPassword(),
                        user.getPhoneNumber(),
                        user.getAddress(),
                        user.getCreatedAt())
                )
                .toList();
    }

    // 사용자 비활성화
    public void activeUser(Long id,boolean active) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));

        user.deactivate(active);
        userRepository.save(user);
    }

    // 사용자 정보 업데이트
    public void updateUser(Long id, UserDto userDto) {
        UserEntity user = userRepository.findById(id)
                .map(u -> {
                    u.updateProfile(
                            userDto.getName(),
                            userDto.getEmail(),
                            userDto.getPhoneNumber(),
                            userDto.getAddress()
                    );
                    return u;
                })
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));

        userRepository.save(user);
    }

    // 사용자 비밀번호 업데이트
    public void resetPassword(Long id, UserPasswordDTO passwordDTO) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));

        user.updatePassword(passwordEncoder.encode(passwordDTO.getPassword()));
        userRepository.save(user);
    }






}
