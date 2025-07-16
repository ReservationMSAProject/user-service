package com.reservation.user.users.repository;

import com.reservation.user.users.domain.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByEmail(String email);
    Optional<UserEntity> findByName(String name);

    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phone);

}
