package com.stuman.auth_service.service.impl;
import com.stuman.auth_service.dto.response.UserInfoResponse;
import com.stuman.auth_service.entity.User;
import com.stuman.auth_service.exception.BaseException;
import com.stuman.auth_service.mapper.UserMapper;
import com.stuman.auth_service.repository.UserRepository;
import com.stuman.auth_service.service.impl.UserServiceImpl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void getUserInfo_shouldReturnUserInfo_whenUserExists() {
        User user = new User();
        user.setId(1L);

        UserInfoResponse response = UserInfoResponse.builder().id(1L).build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toUserInfo(user)).thenReturn(response);

        UserInfoResponse result = userService.getUserInfo(1L);

        assertEquals(1L, result.getId());
    }

    @Test
    void getUserInfo_shouldThrowException_whenUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(BaseException.class, () -> userService.getUserInfo(1L));
    }
}
