package io.oauth.resourceserverrolesresources.repository.impl.mapper;

import io.oauth.resourceserverrolesresources.web.domain.Resource;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ResourceMapper {

    List<Resource> findAll();

}
