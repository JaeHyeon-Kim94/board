package io.oauth2.client.resource;


import java.util.List;
import java.util.Map;

public interface ResourceRepository {

    Resource addRole(Resource resource, String roleId);

    int updateRole(Resource resource, String roleId);

    int deleteResource(String resourceId);

    List<Resource> findAll();

    Resource findById(String resourceId);

    Map<String, Object> findByResources(Long offset, int size);
}
