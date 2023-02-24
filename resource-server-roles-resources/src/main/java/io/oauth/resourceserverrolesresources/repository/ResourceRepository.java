package io.oauth.resourceserverrolesresources.repository;


import io.oauth.resourceserverrolesresources.web.domain.Resource;

import java.util.List;

public interface ResourceRepository {

    Resource addRole(Resource resource, String roleId);
    int updateRole(Resource resource, String roleId);
    int deleteResource(String resourceId);
    List<Resource> findAll();
    Resource findById(String resourceId);
}
