package io.oauth.resourceserverroleresource.repository;

import io.oauth.resourceserverroleresource.web.domain.Role;

import java.util.List;

public interface RoleRepository {

    List<Role> findAll();

}
