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
import com.stuman.auth_service.mapper.AuthMapper;
import com.stuman.auth_service.mapper.UserMapper;
import com.stuman.auth_service.repository.RoleRepository;
import com.stuman.auth_service.repository.UserRepository;
import com.stuman.auth_service.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Set;
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final UserMapper userMapper;
    private final AuthMapper authMapper;
    // REGISTER
    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BaseException(ErrorCode.USER_ALREADY_EXISTS);
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BaseException(ErrorCode.USER_ALREADY_EXISTS);
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        Role role = roleRepository.findByName(RoleName.STUDENT)
                .orElseThrow(() -> new BaseException(ErrorCode.ROLE_NOT_FOUND));

        user.setRoles(Set.of(role));
        userRepository.save(user);
        return login(new LoginRequest(request.getUsername(), request.getPassword()));
    }
    // LOGIN
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
        UserInfoResponse userInfo = userMapper.toUserInfo(user);
        return authMapper.toAuthResponse(jwt, userInfo);
    }
}