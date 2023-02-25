package io.oauth.resourceserverrolesresources.repository.impl.mapper;


import io.oauth.resourceserverrolesresources.web.domain.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {

    User findByUserId(String userId);
    List<User> findAll();

    List<User> findUsers(int offset, int size);

    int findUsersCount();

}
