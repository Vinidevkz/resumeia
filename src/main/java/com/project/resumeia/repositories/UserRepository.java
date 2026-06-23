package com.project.resumeia.repositories;

import com.project.resumeia.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailIgnoreCase(String email);

    @Modifying
    @Query("UPDATE User u SET u.tokensPerDay = 0")
    void resetAllSessions();
}
