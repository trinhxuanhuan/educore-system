package com.educore.auth.repository;

import com.educore.auth.entity.Role;
import com.educore.auth.entity.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName name);
    Set<Role> findByNameIn(Set<RoleName> names);

}
