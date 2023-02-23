package io.oauth.oauth2authorizationclientserver.security.repository.user;

import io.oauth.oauth2authorizationclientserver.web.domain.Role;
import io.oauth.oauth2authorizationclientserver.web.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Set;

@RequiredArgsConstructor
@Repository
public class UserRepositoryMybatis implements UserRepository {

    private final UserMapper userMapper;

    @Override
    public User findByUserId(String username) {
        return userMapper.findByUserId(username);
    }

    @Override
    public User insert(User user) {
        Role defaultUserRole = Role.getDefaultUserRole();
        Set<Role> roles = Set.of(defaultUserRole);
        user.setRoles(roles);
        userMapper.insert(user, defaultUserRole.getId());
        return user;
    }

    @Override
    public void update(User user) {
        userMapper.update(user);
    }

    @Override
    public void updateUserRoles(Set<Role> roles, String userId) {
        userMapper.insertUserRoles(roles, userId);
    }

    @Override
    public void deleteByUserId(String userId) {
        userMapper.deleteByUserId(userId);
    }
}
