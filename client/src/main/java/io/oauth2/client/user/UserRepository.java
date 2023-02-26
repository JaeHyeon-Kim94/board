package io.oauth2.client.user;

import java.util.List;
import java.util.Map;

public interface UserRepository {

    User findByUserId(String userId);

    int addUserRole(String userId, String roleId);

    int deleteUserRole(String userId, String roleId);

    List<User> findAll();

    Map<String, Object> findUsers(Long offset, int size);
}
