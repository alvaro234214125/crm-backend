package com.dev.backend_crm.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Table(name = "activity_logs")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String action;
    private String description;
    private String entityType;
    private Long entityId;
    private String performedBy;

    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp;
}
