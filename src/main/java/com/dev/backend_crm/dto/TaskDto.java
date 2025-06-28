package com.dev.backend_crm.dto;

import com.dev.backend_crm.entity.TaskStatus;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class TaskDto {
    private Long id;
    private String title;
    private String description;
    private Date dueDate;
    private TaskStatus status;
    private Long clientId;
    private Long userId;
}