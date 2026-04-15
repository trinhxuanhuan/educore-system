package com.educore.auth.service;

import com.educore.auth.entity.RoleName;

import java.util.Set;

public interface AdminService {

    void assignRoles(Long userId, Set<RoleName> roleNames);
}