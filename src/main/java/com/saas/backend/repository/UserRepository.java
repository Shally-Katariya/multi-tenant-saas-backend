package com.saas.backend.repository;

import com.saas.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findByTenantId(String tenantId);

    // 🔥 REQUIRED FOR LOGIN
    Optional<User> findByUsernameAndTenantId(String username, String tenantId);
}