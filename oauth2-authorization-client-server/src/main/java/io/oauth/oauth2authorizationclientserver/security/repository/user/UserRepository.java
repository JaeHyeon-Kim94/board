package io.oauth.oauth2authorizationclientserver.security.repository.user;


import io.oauth.oauth2authorizationclientserver.web.domain.User;

public interface UserRepository {

    User findByUserId(String userId);
    User insert(User user);
    void update(User user);
    void deleteByUserId(String userId);

}
