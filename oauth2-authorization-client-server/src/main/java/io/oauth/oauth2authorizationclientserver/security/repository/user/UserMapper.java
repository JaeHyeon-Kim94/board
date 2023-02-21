package io.oauth.oauth2authorizationclientserver.security.repository.user;

import io.oauth.oauth2authorizationclientserver.web.domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {

    User findByUserId(String userId);
    void insert(@Param("user") User user, @Param("roleId") String roleId);
    void update(User user);
    void deleteByUserId(String userId);

}
