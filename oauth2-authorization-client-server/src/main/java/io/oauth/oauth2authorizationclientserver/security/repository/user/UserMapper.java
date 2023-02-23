package io.oauth.oauth2authorizationclientserver.security.repository.user;

import io.oauth.oauth2authorizationclientserver.web.domain.Role;
import io.oauth.oauth2authorizationclientserver.web.domain.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Set;

@Mapper
public interface UserMapper {

    User findByUserId(String userId);
    void insert(@Param("user") User user, @Param("roleId") String roleId);
    void update(User user);

    void insertUserRoles(Set<Role> roles, String userId);

    void deleteByUserId(String userId);

}
