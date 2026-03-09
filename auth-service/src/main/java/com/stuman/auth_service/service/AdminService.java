package com.stuman.auth_service.service;
import com.stuman.auth_service.exception.BaseException;
import com.stuman.auth_service.exception.ErrorCode;
import com.stuman.auth_service.entity.Role;
import com.stuman.auth_service.entity.RoleName;
import com.stuman.auth_service.entity.User;
import com.stuman.auth_service.repository.RoleRepository;
import com.stuman.auth_service.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Transactional
    public void assignRoles(Long userId, Set<RoleName> roleNames) {

        System.out.println(">>> ROLE NAMES: " + roleNames);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));

        Set<Role> roles = roleRepository.findByNameIn(roleNames);

        System.out.println(">>> ROLES FROM DB: " + roles);

        if (roles.size() != roleNames.size()) {
            throw new BaseException(ErrorCode.INVALID_ROLE);
        }

        user.setRoles(roles);
    }
}
