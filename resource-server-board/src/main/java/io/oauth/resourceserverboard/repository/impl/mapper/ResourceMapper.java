package io.oauth.resourceserverboard.repository.impl.mapper;

import io.oauth.resourceserverboard.web.domain.Resource;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ResourceMapper {

    List<Resource> findAll();

}
