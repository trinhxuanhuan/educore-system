package com.stuman.auth_service.service;
import com.stuman.auth_service.dto.response.UserInfoResponse;
import com.stuman.auth_service.entity.User;
import com.stuman.auth_service.exception.BaseException;
import com.stuman.auth_service.exception.ErrorCode;
import com.stuman.auth_service.mapper.UserMapper;
import com.stuman.auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    public UserInfoResponse getUserInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));
        return userMapper.toUserInfo(user);
    }
}