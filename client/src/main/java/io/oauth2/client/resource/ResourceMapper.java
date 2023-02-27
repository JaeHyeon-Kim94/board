package io.oauth2.client.resource;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ResourceMapper {

    int insert(Resource resource, String roleId);
    int update(Resource resource, String roleId);
    int delete(Long resourceId);
    List<Resource> findAll();
    Resource findById(Long resourceId);

    List<Resource> findResources(Long offset, int size);

    long findResourcesCount();
}
