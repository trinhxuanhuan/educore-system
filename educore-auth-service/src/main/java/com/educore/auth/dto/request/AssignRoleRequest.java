package com.educore.auth.dto.request;

import com.educore.auth.entity.RoleName;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.Set;

@Data
public class AssignRoleRequest {
    @NotEmpty
    private Set<RoleName> roles;
}
