package io.oauth2.client.resource;


import java.util.List;
import java.util.Map;

public interface ResourceRepository {

    Resource addResource(Resource resource, String roleId);

    int updateResource(Resource resource, String roleId);

    int deleteResource(Long resourceId);

    List<Resource> findAll();

    Resource findById(Long resourceId);

    Map<String, Object> findResources(Long offset, int size);
}
