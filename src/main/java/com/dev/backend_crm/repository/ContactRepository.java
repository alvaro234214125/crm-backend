package com.dev.backend_crm.repository;

import com.dev.backend_crm.entity.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ContactRepository extends JpaRepository<Contact, Long> {
    List<Contact> findByClientId(Long clientId);
    long countByClientId(Long clientId);
}