package com.dev.backend_crm.repository;

import com.dev.backend_crm.entity.Interaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InteractionRepository extends JpaRepository<Interaction, Long> {
}
