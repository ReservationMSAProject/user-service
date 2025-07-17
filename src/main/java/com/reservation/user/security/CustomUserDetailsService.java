package com.reservation.user.security;

import com.reservation.user.users.dto.UserEntityDto;
import com.reservation.user.users.dto.UserInfoDTO;
import com.reservation.user.users.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        UserEntityDto user = userService.findByUserEmail(email);

        if (user == null) {
            throw new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + email);
        }

        return User.builder()
                .username(user.email())
                .password(user.password())  // 암호화된 비밀번호 필요!
                .authorities("ROLE_" + user.roles())
                .build();
    }

}
