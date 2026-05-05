package com.educore.auth.service.impl;
import com.educore.auth.dto.request.ChangePasswordRequest;
import com.educore.auth.dto.request.LoginRequest;
import com.educore.auth.dto.request.RegisterRequest;
import com.educore.auth.dto.response.AuthResponse;
import com.educore.auth.dto.response.UserInfoResponse;
import com.educore.auth.entity.Role;
import com.educore.auth.entity.RoleName;
import com.educore.auth.entity.User;
import com.educore.auth.exception.BaseException;
import com.educore.auth.exception.ErrorCode;
import com.educore.auth.mapper.AuthMapper;
import com.educore.auth.mapper.UserMapper;
import com.educore.auth.repository.RoleRepository;
import com.educore.auth.repository.UserRepository;
import com.educore.auth.security.JwtProvider;
import com.educore.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
@Service
@Transactional
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final UserMapper userMapper;
    private final AuthMapper authMapper;
    @Override
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
    @Override
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
        return authMapper.toAuthResponse(jwt, userInfo, user.isPasswordChangeRequired());
    }

    /**
     * Rotates the password for the currently authenticated user. The
     * caller must supply their current password (defence in depth even
     * when the JWT is valid) and a new one different from the current.
     * On success the {@code passwordChangeRequired} flag is cleared so
     * subsequent logins no longer prompt for rotation.
     */
    @Override
    public void changePassword(String username, ChangePasswordRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BaseException(ErrorCode.WRONG_CURRENT_PASSWORD);
        }

        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new BaseException(ErrorCode.NEW_PASSWORD_SAME_AS_OLD);
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setPasswordChangeRequired(false);
        userRepository.save(user);
    }
}
