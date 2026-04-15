package com.educore.auth.service.impl;
import com.educore.auth.dto.response.UserInfoResponse;
import com.educore.auth.entity.User;
import com.educore.auth.exception.BaseException;
import com.educore.auth.exception.ErrorCode;
import com.educore.auth.mapper.UserMapper;
import com.educore.auth.repository.UserRepository;
import com.educore.auth.service.UserService;
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
