package org.mvasylchuk.userservice;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface UserRepository extends JpaRepository<UserEntity,Long> {
    List<UserEntity> findAllByBirthDateIsBetween(LocalDate from, LocalDate to);
    boolean existsByEmail(String email);
}
