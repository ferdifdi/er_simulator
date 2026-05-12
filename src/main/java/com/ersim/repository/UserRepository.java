package com.ersim.repository;

import com.ersim.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/** Repository for application users. */
public interface UserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByUsername(String username);
}
