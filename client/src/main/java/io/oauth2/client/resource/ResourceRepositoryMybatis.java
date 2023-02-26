package io.oauth2.client.resource;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

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
    public Map<String, Object> findByResources(Long offset, int size) {
        List<Resource> resources = resourceMapper.findResources(offset, size);
        int totalCount = resourceMapper.findResourcesCount();

        return Map.of("resources", resources, "totalCount", totalCount);
    }

    @Override
    public Resource findById(String resourceId) {
        return resourceMapper.findById(resourceId);
    }
}
