package io.oauth.resourceserverboard.repository;

import io.oauth.resourceserverboard.web.domain.Role;

import java.util.List;

public interface RoleRepository {

    List<Role> findAll();

}
