package com.dev.backend_crm.service;

import com.dev.backend_crm.entity.ActivityLog;
import com.dev.backend_crm.repository.ActivityLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class ActivityLogService {

    private final ActivityLogRepository activityLogRepository;

    public void log(String action, String description, String entityType, Long entityId, String performedBy) {
        ActivityLog log = ActivityLog.builder()
                .action(action)
                .description(description)
                .entityType(entityType)
                .entityId(entityId) 
                .performedBy(performedBy)
                .timestamp(new Date())
                .build();

        activityLogRepository.save(log);
    }
}
