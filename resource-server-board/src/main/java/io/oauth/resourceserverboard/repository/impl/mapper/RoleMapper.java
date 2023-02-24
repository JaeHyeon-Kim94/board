package io.oauth.resourceserverboard.repository.impl.mapper;

import io.oauth.resourceserverboard.web.domain.Role;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RoleMapper {

    List<Role> findAll();

}
