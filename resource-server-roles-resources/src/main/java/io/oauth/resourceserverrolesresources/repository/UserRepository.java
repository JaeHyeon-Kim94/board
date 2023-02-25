package io.oauth.resourceserverrolesresources.repository;

import io.oauth.resourceserverrolesresources.web.domain.User;

import java.util.List;
import java.util.Map;

public interface UserRepository {

    User findByUserId(String userId);
    List<User> findAll();
    Map<String, Object> findUsers(int offset, int size);
}
