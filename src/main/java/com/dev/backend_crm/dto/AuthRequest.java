package com.dev.backend_crm.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class AuthRequest {
    private String email;
    private String password;

}
