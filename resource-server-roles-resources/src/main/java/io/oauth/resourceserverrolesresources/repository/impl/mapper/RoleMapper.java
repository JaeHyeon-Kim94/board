package io.oauth.resourceserverrolesresources.repository.impl.mapper;

import io.oauth.resourceserverrolesresources.web.domain.Role;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RoleMapper {

    List<Role> findAll();

    int insert(Role role, String parentId);

    Role findByName(String roleName);

    int updateRole(Role role, String parentId);

    int deleteRole(String roleId);

    int addRoleOfUser(String userId, String roleId);

    int updateRoleOfUser(String userId, String roleId);
}
