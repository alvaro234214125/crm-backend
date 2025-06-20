package com.dev.backend_crm.dto;

import com.dev.backend_crm.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class UserDto {
    private Long id;
    private String name;
    private String email;
    private String password;
    private Boolean status;
    private RoleDto role;
}
