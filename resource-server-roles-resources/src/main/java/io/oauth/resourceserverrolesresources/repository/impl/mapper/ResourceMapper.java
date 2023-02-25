package io.oauth.resourceserverrolesresources.repository.impl.mapper;

import io.oauth.resourceserverrolesresources.web.domain.Resource;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface ResourceMapper {

    int insert(Resource resource, String roleId);
    int update(Resource resource, String roleId);
    int delete(String resourceId);
    List<Resource> findAll();
    Resource findById(String resourceId);

    List<Resource> findResources(int offset, int size);

    int findResourcesCount();
}
