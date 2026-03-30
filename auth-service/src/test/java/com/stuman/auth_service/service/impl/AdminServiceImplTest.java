package com.stuman.auth_service.service.impl;

import com.stuman.auth_service.entity.Role;
import com.stuman.auth_service.entity.RoleName;
import com.stuman.auth_service.entity.User;
import com.stuman.auth_service.exception.BaseException;
import com.stuman.auth_service.repository.RoleRepository;
import com.stuman.auth_service.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class AdminServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private AdminServiceImpl adminService;

    @Test
    void assignRoles_shouldUpdateRoles_whenValid() {
        User user = new User();
        user.setId(1L);

        Role role = new Role();
        role.setName(RoleName.ADMIN);

        Set<RoleName> roleNames = Set.of(RoleName.ADMIN);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(roleRepository.findByNameIn(roleNames)).thenReturn(Set.of(role));

        adminService.assignRoles(1L, roleNames);

        assertEquals(1, user.getRoles().size());
    }

    @Test
    void assignRoles_shouldThrowException_whenRoleInvalid() {
        User user = new User();

        Set<RoleName> roleNames = Set.of(RoleName.ADMIN);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(roleRepository.findByNameIn(roleNames)).thenReturn(Set.of());

        assertThrows(BaseException.class,
                () -> adminService.assignRoles(1L, roleNames));
    }
}
