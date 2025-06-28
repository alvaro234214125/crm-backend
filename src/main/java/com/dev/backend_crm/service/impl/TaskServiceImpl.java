package com.dev.backend_crm.service.impl;

import com.dev.backend_crm.dto.TaskDto;
import com.dev.backend_crm.dto.TaskStatsDto;
import com.dev.backend_crm.entity.PageResponse;
import com.dev.backend_crm.entity.Task;
import com.dev.backend_crm.entity.User;
import com.dev.backend_crm.entity.Client;
import com.dev.backend_crm.entity.TaskStatus;
import com.dev.backend_crm.repository.TaskRepository;
import com.dev.backend_crm.repository.UserRepository;
import com.dev.backend_crm.repository.ClientRepository;
import com.dev.backend_crm.service.ActivityLogService;
import com.dev.backend_crm.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final ClientRepository clientRepository;
    private final ActivityLogService activityLogService;

    @Override
    public PageResponse<TaskDto> getAll(Pageable pageable) {
        Page<Task> page = taskRepository.findAll(pageable);
        List<TaskDto> content = page.getContent().stream().map(this::toDto).collect(Collectors.toList());
        return new PageResponse<>(content, page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages(), page.isLast());
    }

    @Override
    public TaskDto getById(Long id) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new RuntimeException("Task not found"));
        return toDto(task);
    }

    @Override
    public TaskDto create(TaskDto dto, String performedBy) {
        Task saved = taskRepository.save(fromDto(dto));
        activityLogService.log("CREATE_TASK", "Creó la tarea: " + saved.getTitle(), "Task", saved.getId(), performedBy);
        return toDto(saved);
    }

    @Override
    public TaskDto update(Long id, TaskDto dto, String performedBy) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new RuntimeException("Task not found"));
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setDueDate(dto.getDueDate());
        task.setStatus(dto.getStatus());

        task.setClient(clientRepository.findById(dto.getClientId()).orElseThrow(() -> new RuntimeException("Client not found")));
        task.setUser(userRepository.findById(dto.getUserId()).orElseThrow(() -> new RuntimeException("User not found")));

        Task updated = taskRepository.save(task);
        activityLogService.log("UPDATE_TASK", "Actualizó la tarea: " + updated.getTitle(), "Task", updated.getId(), performedBy);
        return toDto(updated);
    }

    @Override
    public void delete(Long id, String performedBy) {
        if (!taskRepository.existsById(id)) throw new RuntimeException("Task not found");
        taskRepository.deleteById(id);
        activityLogService.log("DELETE_TASK", "Eliminó la tarea con ID: " + id, "Task", id, performedBy);
    }

    @Override
    public PageResponse<TaskDto> search(String title, TaskStatus status, Pageable pageable) {
        Specification<Task> spec = (root, query, cb) -> cb.conjunction();
        if (title != null && !title.isBlank()) spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%"));
        if (status != null) spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status));

        Page<Task> page = taskRepository.findAll(spec, pageable);
        List<TaskDto> content = page.getContent().stream().map(this::toDto).collect(Collectors.toList());
        return new PageResponse<>(content, page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages(), page.isLast());
    }

    @Override
    public TaskStatsDto getStats() {
        long total = taskRepository.count();
        long completadas = taskRepository.countByStatus(TaskStatus.Completed);
        long pendientes = taskRepository.countByStatus(TaskStatus.Pending);
        return new TaskStatsDto(total, completadas, pendientes);
    }

    private TaskDto toDto(Task t) {
        return TaskDto.builder()
                .id(t.getId())
                .title(t.getTitle())
                .description(t.getDescription())
                .dueDate(t.getDueDate())
                .status(t.getStatus())
                .clientId(t.getClient().getId())
                .userId(t.getUser().getId())
                .build();
    }

    private Task fromDto(TaskDto dto) {
        return Task.builder()
                .id(dto.getId())
                .title(dto.getTitle())
                .description(dto.getDescription())
                .dueDate(dto.getDueDate())
                .status(dto.getStatus())
                .client(clientRepository.findById(dto.getClientId()).orElseThrow(() -> new RuntimeException("Client not found")))
                .user(userRepository.findById(dto.getUserId()).orElseThrow(() -> new RuntimeException("User not found")))
                .build();
    }
}