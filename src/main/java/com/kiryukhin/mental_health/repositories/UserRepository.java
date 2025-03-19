package com.kiryukhin.mental_health.repositories;

import com.kiryukhin.mental_health.models.AuthProvider;
import com.kiryukhin.mental_health.models.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u.username FROM User u WHERE u.username LIKE :pattern")
    Set<String> findUsernameSetByUsernameLike(@Param("pattern") String pattern);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    @Modifying
    @Query("UPDATE User u SET u.authProvider = :authProvider WHERE u.username = :username")
    void updateAuthProvider(@Param("username") String username, @Param("authProvider") AuthProvider authProvider);

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    Optional<User> findByUsernameOrEmail(String username, String email);

    User getByEmail(@NotBlank @Email String email);

    User getByUsernameOrEmail(@NotBlank String username, @NotBlank @Email String email);

    Optional<User> findByUsernameAndIsBlockedFalse(String username);
}