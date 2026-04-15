package com.educore.auth.service.impl;
import com.educore.auth.dto.response.UserInfoResponse;
import com.educore.auth.entity.User;
import com.educore.auth.exception.BaseException;
import com.educore.auth.mapper.UserMapper;
import com.educore.auth.repository.UserRepository;

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
