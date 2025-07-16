package com.reservation.user.users.dto;

import com.reservation.user.users.enums.Roles;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoDTO {

    private Long id;
    private String name;
    private String email;
    private Roles role;


}
