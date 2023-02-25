package io.oauth.resourceserverrolesresources.user;


import io.oauth.resourceserverrolesresources.user.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {

    User findByUserId(String userId);

    List<User> findAll();

    int addUserRole(String userId, String roleId);

    int updateUserRole(String userId, String roleId);

    List<User> findUsers(int offset, int size);

    int findUsersCount();

}
