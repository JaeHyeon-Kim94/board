package io.oauth2.client.user;


import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {

    User findByUserId(String userId);

    List<User> findAll();

    int addUserRole(String userId, String roleId);

    int deleteUserRole(String userId, String roleId);

    List<User> findUsers(Long offset, int size);

    int findUsersCount();

}
