package com.stuman.auth_service;

import com.stuman.auth_service.entity.Role;
import com.stuman.auth_service.entity.RoleName;
import com.stuman.auth_service.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    public DataInitializer(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) {

        createRoleIfNotExists(RoleName.ADMIN);
        createRoleIfNotExists(RoleName.TEACHER);
        createRoleIfNotExists(RoleName.STUDENT);
    }

    private void createRoleIfNotExists(RoleName roleName) {
        if (roleRepository.findByName(roleName).isEmpty()) {
            roleRepository.save(
                    Role.builder()
                            .name(roleName)
                            .build()
            );
        }
    }
}
