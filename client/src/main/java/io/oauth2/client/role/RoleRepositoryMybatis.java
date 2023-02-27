package io.oauth2.client.role;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Repository
public class RoleRepositoryMybatis implements RoleRepository {

    private final RoleMapper roleMapper;

    @Override
    public int addRole(Role role, String parentId) {
        return roleMapper.insert(role, parentId);
    }

    @Override
    public Role findById(String id) {
        return roleMapper.findById(id);
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
    public List<Role> findAll() {
        return roleMapper.findAll();
    }

    @Override
    public Map<String, Object> findRoles(Long offset, int size) {

        List<Role> roles = roleMapper.findRoles(offset, size);
        int totalCount = roleMapper.findRolesCount();

        return Map.of("roles", roles, "totalCount", totalCount);
    }
}
