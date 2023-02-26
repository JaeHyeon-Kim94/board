package io.oauth2.client.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Repository
public class UserRepositoryMybatis implements UserRepository {

    private final UserMapper userMapper;


    @Override
    public User findByUserId(String userId) {
        return userMapper.findByUserId(userId);
    }

    @Override
    public int addUserRole(String userId, String roleId) {
        return userMapper.addUserRole(userId, roleId);
    }

    @Override
    public int deleteUserRole(String userId, String roleId) {
        return userMapper.deleteUserRole(userId, roleId);
    }

    @Override
    public List<User> findAll() {
        return userMapper.findAll();
    }

    @Override
    public Map<String, Object> findUsers(Long offset, int size) {

        List<User> users = userMapper.findUsers(offset, size);
        int totalCount = userMapper.findUsersCount();
        return Map.of("users", users, "totalCount", totalCount);
    }
}
