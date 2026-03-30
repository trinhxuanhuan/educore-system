package com.stuman.auth_service.service;

import com.stuman.auth_service.entity.RoleName;

import java.util.Set;

public interface AdminService {

    void assignRoles(Long userId, Set<RoleName> roleNames);
}