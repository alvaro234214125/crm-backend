package com.dev.backend_crm.dto;

import com.dev.backend_crm.entity.Role;
import lombok.Data;

@Data
public class RegisterRequest {
    private String name;
    private String email;
    private String password;
    private Boolean status;
    private RoleDto role;
}