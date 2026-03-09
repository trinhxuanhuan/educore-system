package com.stuman.auth_service.controller;

import com.stuman.auth_service.dto.request.AssignRoleRequest;
import com.stuman.auth_service.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminController {

    private final AdminService adminService;

    @PutMapping("/users/{id}/roles")
    public ResponseEntity<Void> assignRoles(
            @PathVariable("id") Long id,
            @RequestBody AssignRoleRequest request
    ) {
        adminService.assignRoles(id, request.getRoles());
        return ResponseEntity.noContent().build();
    }

}
