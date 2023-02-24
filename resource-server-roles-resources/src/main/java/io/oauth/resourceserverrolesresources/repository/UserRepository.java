package io.oauth.resourceserverrolesresources.repository;

import io.oauth.resourceserverrolesresources.web.domain.User;

import java.util.List;

public interface UserRepository {

    User findByUserId(String userId);
    List<User> findAll();

}
