package com.educore.auth.service.impl;
import com.educore.auth.dto.request.LoginRequest;
import com.educore.auth.dto.request.RegisterRequest;
import com.educore.auth.dto.response.AuthResponse;
import com.educore.auth.dto.response.UserInfoResponse;
import com.educore.auth.entity.Role;
import com.educore.auth.entity.RoleName;
import com.educore.auth.entity.User;
import com.educore.auth.exception.BaseException;
import com.educore.auth.mapper.AuthMapper;
import com.educore.auth.mapper.UserMapper;
import com.educore.auth.repository.RoleRepository;
import com.educore.auth.repository.UserRepository;
import com.educore.auth.security.JwtProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private UserMapper userMapper;

    @Mock
    private AuthMapper authMapper;

    @InjectMocks
    private AuthServiceImpl authService;

    private User user;
    private Role role;

    @BeforeEach
    void setup() {
        role = new Role();
        role.setName(RoleName.STUDENT);

        user = new User();
        user.setUsername("huan");
        user.setEmail("huan@gmail.com");
        user.setPassword("encoded");
        user.setRoles(Set.of(role));
    }

    // ================= REGISTER =================

    @Test
    void register_shouldThrowException_whenUsernameExists() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("huan");
        request.setEmail("huan@gmail.com");

        when(userRepository.existsByUsername("huan")).thenReturn(true);

        assertThrows(BaseException.class, () -> authService.register(request));
    }

    @Test
    void register_shouldThrowException_whenEmailExists() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("huan");
        request.setEmail("huan@gmail.com");

        when(userRepository.existsByUsername("huan")).thenReturn(false);
        when(userRepository.existsByEmail("huan@gmail.com")).thenReturn(true);

        assertThrows(BaseException.class, () -> authService.register(request));
    }

    @Test
    void register_shouldReturnAuthResponse_whenValid() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("huan");
        request.setPassword("123456");
        request.setEmail("huan@gmail.com");

        when(userRepository.existsByUsername(any())).thenReturn(false);
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(passwordEncoder.encode("123456")).thenReturn("encoded");
        when(roleRepository.findByName(RoleName.STUDENT)).thenReturn(Optional.of(role));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // mock login flow
        Authentication auth = mock(Authentication.class);
        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(jwtProvider.generateToken(auth)).thenReturn("token");
        when(userRepository.findByUsername("huan")).thenReturn(Optional.of(user));

        UserInfoResponse userInfo = UserInfoResponse.builder()
                .username("huan")
                .email("huan@gmail.com")
                .build();

        AuthResponse expected = new AuthResponse("token", userInfo);

        when(userMapper.toUserInfo(user)).thenReturn(userInfo);
        when(authMapper.toAuthResponse("token", userInfo)).thenReturn(expected);

        AuthResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals("token", response.getAccessToken());
    }

    // ================= LOGIN =================

    @Test
    void login_shouldReturnToken_whenValidCredentials() {
        LoginRequest request = new LoginRequest("huan", "123");

        Authentication auth = mock(Authentication.class);

        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(jwtProvider.generateToken(auth)).thenReturn("token");
        when(userRepository.findByUsername("huan")).thenReturn(Optional.of(user));

        UserInfoResponse userInfo = UserInfoResponse.builder().username("huan").build();
        AuthResponse expected = new AuthResponse("token", userInfo);

        when(userMapper.toUserInfo(user)).thenReturn(userInfo);
        when(authMapper.toAuthResponse("token", userInfo)).thenReturn(expected);

        AuthResponse response = authService.login(request);

        assertEquals("token", response.getAccessToken());
    }

    @Test
    void login_shouldThrowException_whenUserNotFound() {
        LoginRequest request = new LoginRequest("huan", "123");

        Authentication auth = mock(Authentication.class);

        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(jwtProvider.generateToken(auth)).thenReturn("token");
        when(userRepository.findByUsername("huan")).thenReturn(Optional.empty());

        assertThrows(BaseException.class, () -> authService.login(request));
    }
}