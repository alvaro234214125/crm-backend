package com.dev.backend_crm.repository;

import com.dev.backend_crm.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository <User, Long> {
    Optional <User> findByEmail(String email);
    boolean existsByEmail(String email);
    long count();
    long countByStatus(boolean status);
}
