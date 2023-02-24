package io.oauth.oauth2authorizationclientserver.repository.user;


import io.oauth.oauth2authorizationclientserver.web.domain.Role;
import io.oauth.oauth2authorizationclientserver.web.domain.User;

import java.util.Set;

public interface UserRepository {

    User findByUserId(String userId);
    User insert(User user);
    void update(User user);

}
