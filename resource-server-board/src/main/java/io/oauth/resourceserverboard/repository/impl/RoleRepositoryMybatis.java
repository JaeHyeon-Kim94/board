package io.oauth.resourceserverboard.repository.impl;

import io.oauth.resourceserverboard.repository.RoleRepository;
import io.oauth.resourceserverboard.repository.impl.mapper.RoleMapper;
import io.oauth.resourceserverboard.web.domain.Role;
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
}
