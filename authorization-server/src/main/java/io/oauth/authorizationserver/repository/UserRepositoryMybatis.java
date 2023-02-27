package io.oauth.authorizationserver.repository;

import io.oauth.authorizationserver.web.domain.Role;
import io.oauth.authorizationserver.web.domain.User;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepositoryMybatis implements UserRepository{

    private final UserMapper userMapper;

    public UserRepositoryMybatis(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public User findByUserId(String userId) {
        User user = userMapper.findByUserId(userId);
        return user;
    }

    @Override
    public User insert(User user) {
        Role defaultUserRole = Role.getDefaultUserRole();
        user.getRoles().add(defaultUserRole);

        userMapper.insert(user, defaultUserRole.getId());
        return user;
    }

    @Override
    public void update(User user) {
        userMapper.update(user);
    }

    @Override
    public boolean isDuplicate(String type, String value) {
        return userMapper.isDuplicate(type, value);
    }
}
