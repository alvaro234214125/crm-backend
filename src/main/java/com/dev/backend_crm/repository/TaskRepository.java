package com.dev.backend_crm.repository;

import com.dev.backend_crm.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {
    long countByStatus(com.dev.backend_crm.entity.TaskStatus status);
}
