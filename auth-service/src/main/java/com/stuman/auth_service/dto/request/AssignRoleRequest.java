package com.stuman.auth_service.dto.request;

import com.stuman.auth_service.entity.RoleName;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.Set;

@Data
public class AssignRoleRequest {
    @NotEmpty
    private Set<RoleName> roles;
}
