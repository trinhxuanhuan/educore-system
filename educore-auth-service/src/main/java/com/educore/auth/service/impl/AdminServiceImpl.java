package com.educore.auth.service.impl;

import com.educore.auth.entity.Role;
import com.educore.auth.entity.RoleName;
import com.educore.auth.entity.User;
import com.educore.auth.exception.BaseException;
import com.educore.auth.exception.ErrorCode;
import com.educore.auth.repository.RoleRepository;
import com.educore.auth.repository.UserRepository;
import com.educore.auth.service.AdminService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Set;
@Service
@RequiredArgsConstructor
@Transactional
public class AdminServiceImpl implements AdminService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    @Override
    public void assignRoles(Long userId, Set<RoleName> roleNames) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));
        Set<Role> roles = roleRepository.findByNameIn(roleNames);
        if (roles.size() != roleNames.size()) {
            throw new BaseException(ErrorCode.INVALID_ROLE);
        }
        user.setRoles(roles);
    }
}