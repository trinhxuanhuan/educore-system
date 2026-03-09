package com.stuman.auth_service.service;

import com.stuman.auth_service.exception.BaseException;
import com.stuman.auth_service.exception.ErrorCode;
import com.stuman.auth_service.dto.request.LoginRequest;
import com.stuman.auth_service.dto.request.RegisterRequest;
import com.stuman.auth_service.dto.response.AuthResponse;
import com.stuman.auth_service.dto.response.UserInfoResponse;
import com.stuman.auth_service.entity.Role;
import com.stuman.auth_service.entity.RoleName;
import com.stuman.auth_service.entity.User;
import com.stuman.auth_service.repository.RoleRepository;
import com.stuman.auth_service.repository.UserRepository;
import com.stuman.auth_service.security.JwtProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;

    public AuthService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtProvider jwtProvider) {

        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtProvider = jwtProvider;
    }

    public AuthResponse register(RegisterRequest request) {

        // Check tồn tại username
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BaseException(ErrorCode.USER_ALREADY_EXISTS);
        }

        // Check tồn tại email
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BaseException(ErrorCode.USER_ALREADY_EXISTS);
        }

        // Tạo user mới
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        // Assign role mặc định: STUDENT
        Optional<Role> studentRole = roleRepository.findByName(RoleName.STUDENT);

        if (studentRole.isEmpty()) {
            throw new BaseException(ErrorCode.ROLE_NOT_FOUND);
        }

        Set<Role> roles = new HashSet<>();
        roles.add(studentRole.get());

        user.setRoles(roles);

        userRepository.save(user);

        // Login ngay sau register
        return login(new LoginRequest(request.getUsername(), request.getPassword()));
    }


    public AuthResponse login(LoginRequest request) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtProvider.generateToken(authentication);

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));

        Set<String> roles = user.getRoles()
                .stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toSet());

        UserInfoResponse userInfo = new UserInfoResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                roles
        );

        return new AuthResponse(jwt, userInfo);
    }
}