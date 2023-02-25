package io.oauth.resourceserverrolesresources.role;

import java.util.List;
import java.util.Map;

public interface RoleRepository {

    int addRole(Role role, String parentId);

    Role findByName(String roleName);

    Role findById(String id);

    int updateRole(Role role, String parentId);

    int deleteRole(String roleId);

    List<Role> findAll();

    Map<String, Object> findRoles(int offset, int size);

}