package com.reservation.user.users.controller;

import com.reservation.user.users.dto.UserDto;
import com.reservation.user.users.dto.UserEntityDto;
import com.reservation.user.users.dto.UserPasswordRequestDTO;
import com.reservation.user.users.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Enumeration;
import java.util.List;

import org.springframework.security.core.userdetails.User;


@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/user/api/v1")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserEntityDto> getMyInfo(@AuthenticationPrincipal User principal
            , HttpServletRequest request) {

        log.info("=== Request Headers ===");
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            log.info("Header: {} = {}", headerName, headerValue);
        }

        log.info("Getting user info for principal: {}", principal.getUsername());
        // principal.getUsername()이 이메일(또는 유니크 식별자)라고 가정
        UserEntityDto user = userService.findByUserEmail(principal.getUsername());
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }


    // 이메일로 사용자 조회
    @GetMapping("/user")
    public ResponseEntity<UserEntityDto> getUser(@RequestParam("email") @Email @NotBlank String email) {
        log.info("Getting user with email {}", email);
        UserEntityDto userEmail = userService.findByUserEmail(email);
        if (userEmail == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(userEmail);
    }

    // 아이디로 사용자 조회
    @GetMapping("/users")
    public ResponseEntity<List<UserEntityDto>> getAllUsers() {
        return ResponseEntity.ok(userService.findAllUsers());
    }

    // 사용자 업데이트
    @PostMapping("/user/update/{id}")
    public ResponseEntity<?> updateUser(@PathVariable("id") Long id, @RequestBody @Valid UserDto userDto) {
        userService.updateUser(id, userDto);
        return ResponseEntity.ok("User updated successfully");
    }

    // 사용자 활성화 상태 업데이트
    @PostMapping("/user/active/{id}")
    public ResponseEntity<?> activeUser(@PathVariable("id") Long id, @RequestParam("active") boolean active) {
        userService.activeUser(id, active);
        return ResponseEntity.ok("User active status updated successfully");
    }

    // 사용자 비밀번호 재설정
    @PostMapping("/user/password-reset/{id}")
    public ResponseEntity<?> resetPassword(@PathVariable("id") Long id, @RequestBody @Valid UserPasswordRequestDTO passwordDTO) {
        userService.resetPassword(id, passwordDTO);
        return ResponseEntity.ok("User password reset successfully");
    }


}
