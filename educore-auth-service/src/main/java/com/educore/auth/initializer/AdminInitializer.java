package com.educore.auth.initializer;

import com.educore.auth.entity.Role;
import com.educore.auth.entity.RoleName;
import com.educore.auth.entity.User;
import com.educore.auth.repository.RoleRepository;
import com.educore.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Bootstraps a single ADMIN account on first startup, only if no admin
 * exists yet. Credentials come from environment variables — never commit
 * real defaults to source control.
 *
 * Required env vars (only when an admin still needs to be created):
 *   - ADMIN_INIT_PASSWORD   no default; boot fails if absent at first run
 *
 * Optional env vars (have safe defaults):
 *   - ADMIN_USERNAME        defaults to "admin"
 *   - ADMIN_EMAIL           defaults to "admin@educore.local"
 *
 * The created admin has {@code passwordChangeRequired = true}, so the
 * operator MUST change the password through {@code POST /api/v1/auth/change-password}
 * after their first login. Logins succeed but most other flows can choose
 * to honour the flag and force a password rotation.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.init.username:admin}")
    private String adminUsername;

    @Value("${admin.init.email:admin@educore.local}")
    private String adminEmail;

    @Value("${admin.init.password:#{null}}")
    private String adminPassword;

    @Override
    public void run(String... args) {

        if (userRepository.existsByUsername(adminUsername)) {
            log.debug("Admin user '{}' already exists — skipping bootstrap", adminUsername);
            return;
        }

        if (adminPassword == null || adminPassword.isBlank()) {
            throw new IllegalStateException(
                    "No admin user exists yet and ADMIN_INIT_PASSWORD is not set. " +
                    "Provide a strong initial password via the ADMIN_INIT_PASSWORD " +
                    "environment variable; the admin will be required to rotate it " +
                    "on first login."
            );
        }

        Role adminRole = roleRepository.findByName(RoleName.ADMIN)
                .orElseGet(() -> roleRepository.save(
                        Role.builder()
                                .name(RoleName.ADMIN)
                                .build()
                ));

        User admin = User.builder()
                .username(adminUsername)
                .email(adminEmail)
                .password(passwordEncoder.encode(adminPassword))
                .roles(Set.of(adminRole))
                .passwordChangeRequired(true)
                .build();

        userRepository.save(admin);

        log.info(
                "Bootstrapped admin user '{}'. Password rotation is required on first login.",
                adminUsername
        );
    }
}
