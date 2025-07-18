package com.reservation.user.users.domain;

import com.reservation.user.users.enums.Roles;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Builder
@Getter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="users")
@EntityListeners(AuditingEntityListener.class)
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(unique = true, name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "phone_number", unique = true)
    private String phoneNumber;

    //TODO : 주소는 나중에 카카오에서 제공하는 주소 API를 사용하여 변경 예정
    @Column(name = "address")
    private String address;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Roles role;
    
    private String picture;
    private String provider;    // google, github, kakao 등
    private String providerId;  // OAuth2 제공자의 사용자 ID

    @Column(name = "created_at")
    @CreatedDate
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Column(name = "is_active", columnDefinition = "boolean default true")
    private boolean isActive = true;

    @OneToOne(mappedBy = "userEntity", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private RefreshToken refreshToken;


    public UserEntity updateOAuth2Info(String name, String picture) {
        this.name = name;
        this.picture = picture;
        return this;
    }

    // 프로필 업데이트 메서드 (name, email, phoneNumber, address)
    public void updateProfile(String name, String email, String phoneNumber, String address) {

        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }

    // 비밀번호 업데이트 메서드
    public void updatePassword(String newPassword) {
        this.password = newPassword;
    }

    // 역할 업데이트 메서드
    public void updateRole(Roles newRole) {
        this.role = newRole;
    }

    // 비활성화 메서드 (isActive = false)
    public void deactivate(boolean deactivate) {
        this.isActive = deactivate;
    }

}
