package io.oauth.authorizationserver.repository;

import io.oauth.authorizationserver.web.domain.User;

public interface UserRepository {
    User findByUserId(String userId);

    User insert(User user);

    void update(User user);

    boolean isDuplicate(String type, String value);
}
