package com.dev.backend_crm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class UserListDto {
    private Long id;
    private String name;
    private String email;
    private Boolean status;
    private RoleDto role;
}
