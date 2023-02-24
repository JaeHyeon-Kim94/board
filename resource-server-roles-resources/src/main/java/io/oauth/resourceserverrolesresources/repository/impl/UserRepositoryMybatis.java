package io.oauth.resourceserverrolesresources.repository.impl;

import io.oauth.resourceserverrolesresources.repository.UserRepository;
import io.oauth.resourceserverrolesresources.repository.impl.mapper.UserMapper;
import io.oauth.resourceserverrolesresources.web.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class UserRepositoryMybatis implements UserRepository {

    private final UserMapper userMapper;


    @Override
    public User findByUserId(String userId) {
        return userMapper.findByUserId(userId);
    }

    @Override
    public List<User> findAll() {
        return userMapper.findAll();
    }
}
