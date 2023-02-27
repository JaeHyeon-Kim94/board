package io.oauth.authorizationserver.repository;

import io.oauth.authorizationserver.web.domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {

    User findByUserId(String userId);

    void insert(@Param("user") User user, @Param("roleId") String roleId);

    void update(User user);

    boolean isDuplicate(String type, String value);

}
