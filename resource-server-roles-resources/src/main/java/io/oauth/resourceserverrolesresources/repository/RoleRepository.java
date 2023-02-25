package io.oauth.resourceserverrolesresources.repository;

import io.oauth.resourceserverrolesresources.web.domain.Role;

import java.util.List;
import java.util.Map;

public interface RoleRepository {

    int addRole(Role role, String parentId);

    Role findByName(String roleName);

    int updateRole(Role role, String parentId);

    int deleteRole(String roleId);

    int addRoleOfUser(String userId, String roleId);

    int updateRoleOfUser(String userId, String roleId);

    List<Role> findAll();

    Map<String, Object> findRoles(int offset, int size);
}
