package com.dev.backend_crm.repository;

import com.dev.backend_crm.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository <Role, Long> {
    Optional <Role> findRoleById(Long roleId);
}
