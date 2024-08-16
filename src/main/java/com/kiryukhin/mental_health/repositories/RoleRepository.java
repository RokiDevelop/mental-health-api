package com.kiryukhin.mental_health.repositories;

import com.kiryukhin.mental_health.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {}