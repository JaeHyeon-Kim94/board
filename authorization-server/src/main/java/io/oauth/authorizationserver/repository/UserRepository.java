package io.oauth.authorizationserver.repository;

import io.oauth.authorizationserver.web.domain.User;

public interface UserRepository {
    User findByUsername(String username);

    User save(User user);

    boolean isDuplicated(String type, String value);
}
