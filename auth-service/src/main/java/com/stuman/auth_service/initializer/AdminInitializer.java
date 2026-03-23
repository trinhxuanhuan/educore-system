package com.stuman.auth_service.initializer;

import com.stuman.auth_service.entity.Role;
import com.stuman.auth_service.entity.RoleName;
import com.stuman.auth_service.entity.User;
import com.stuman.auth_service.repository.RoleRepository;
import com.stuman.auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        if (userRepository.existsByUsername("admin")) {
            return;
        }

        Role adminRole = roleRepository.findByName(RoleName.ADMIN)
                .orElseGet(() -> roleRepository.save(
                        Role.builder()
                                .name(RoleName.ADMIN)
                                .build()
                ));

        User admin = User.builder()
                .username("admin")
                .email("admin@stuman.com")
                .password(passwordEncoder.encode("admin123"))
                .roles(Set.of(adminRole))
                .build();

        userRepository.save(admin);

        System.out.println(">>> Admin account created");
    }
}