package io.oauth.resourceserverrolesresources.user;

import io.oauth.resourceserverrolesresources.user.User;

import java.util.List;
import java.util.Map;

public interface UserRepository {

    User findByUserId(String userId);

    int addUserRole(String userId, String roleId);

    int updateUserRole(String userId, String roleId);

    List<User> findAll();

    Map<String, Object> findUsers(int offset, int size);
}
