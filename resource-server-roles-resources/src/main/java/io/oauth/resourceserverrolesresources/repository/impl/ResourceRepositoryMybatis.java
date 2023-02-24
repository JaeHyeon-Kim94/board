package io.oauth.resourceserverrolesresources.repository.impl;

import io.oauth.resourceserverrolesresources.repository.ResourceRepository;
import io.oauth.resourceserverrolesresources.repository.impl.mapper.ResourceMapper;
import io.oauth.resourceserverrolesresources.web.domain.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class ResourceRepositoryMybatis implements ResourceRepository {

    private final ResourceMapper resourceMapper;


    @Override
    public Resource addRole(Resource resource, String roleId) {
        resourceMapper.insert(resource, roleId);
        return resource;
    }

    @Override
    public int updateRole(Resource resource, String roleId) {
        return resourceMapper.update(resource, roleId);
    }

    @Override
    public int deleteResource(String resourceId) {
        return resourceMapper.delete(resourceId);
    }

    @Override
    public List<Resource> findAll() {
        return resourceMapper.findAll();
    }

    @Override
    public Resource findById(String resourceId) {
        return resourceMapper.findById(resourceId);
    }
}
