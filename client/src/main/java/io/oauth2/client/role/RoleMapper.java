package io.oauth2.client.role;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RoleMapper {

    int insert(Role role, String parentId);

    Role findByName(String roleName);

    Role findById(String roleId);

    int updateRole(Role role, String parentId);

    int deleteRole(String roleId);

    List<Role> findAll();

    List<Role> findRoles(Long offset, int size);

    int findRolesCount();
}
