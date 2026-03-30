package com.stuman.auth_service.service.impl;
import com.stuman.auth_service.dto.response.UserInfoResponse;
import com.stuman.auth_service.entity.User;
import com.stuman.auth_service.exception.BaseException;
import com.stuman.auth_service.exception.ErrorCode;
import com.stuman.auth_service.mapper.UserMapper;
import com.stuman.auth_service.repository.UserRepository;
import com.stuman.auth_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    @Override
    public UserInfoResponse getUserInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));
        return userMapper.toUserInfo(user);
    }
}
