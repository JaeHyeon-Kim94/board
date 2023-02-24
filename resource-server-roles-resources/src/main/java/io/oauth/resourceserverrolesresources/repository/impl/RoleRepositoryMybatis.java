package io.oauth.resourceserverrolesresources.repository.impl;

import io.oauth.resourceserverrolesresources.repository.RoleRepository;
import io.oauth.resourceserverrolesresources.repository.impl.mapper.RoleMapper;
import io.oauth.resourceserverrolesresources.web.domain.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class RoleRepositoryMybatis implements RoleRepository {

    private final RoleMapper roleMapper;

    @Override
    public List<Role> findAll() {
        return roleMapper.findAll();
    }

    @Override
    public int addRole(Role role, String parentId) {
        return roleMapper.insert(role, parentId);
    }

    @Override
    public Role findByName(String roleName) {
        return roleMapper.findByName(roleName);
    }

    @Override
    public int updateRole(Role role, String parentId) {
        return roleMapper.updateRole(role, parentId);
    }

    @Override
    public int deleteRole(String roleId) {
        return roleMapper.deleteRole(roleId);
    }

    @Override
    public int addRoleOfUser(String userId, String roleId) {
        return roleMapper.addRoleOfUser(userId, roleId);
    }

    @Override
    public int updateRoleOfUser(String userId, String roleId) {
        return roleMapper.updateRoleOfUser(userId, roleId);
    }
}
