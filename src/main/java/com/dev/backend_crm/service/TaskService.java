package com.dev.backend_crm.service;

import com.dev.backend_crm.dto.TaskDto;
import com.dev.backend_crm.dto.TaskStatsDto;
import com.dev.backend_crm.entity.PageResponse;
import com.dev.backend_crm.entity.TaskStatus;
import org.springframework.data.domain.Pageable;

public interface TaskService {
    PageResponse<TaskDto> getAll(Pageable pageable);
    TaskDto getById(Long id);
    TaskDto create(TaskDto dto, String performedBy);
    TaskDto update(Long id, TaskDto dto, String performedBy);
    void delete(Long id, String performedBy);
    PageResponse<TaskDto> search(String title, TaskStatus status, Pageable pageable);
    TaskStatsDto getStats();
}
