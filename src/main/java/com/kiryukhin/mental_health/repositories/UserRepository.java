package com.kiryukhin.mental_health.repositories;

import com.kiryukhin.mental_health.models.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsernameAndIsEnabledTrue(String username);
}