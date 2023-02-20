package io.oauth.oauth2authorizationclientserver.security.repository.socialuser;

import io.oauth.oauth2authorizationclientserver.web.domain.SocialUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SocialUserMapper {

    SocialUser findByUsernameAndClientRegistrationId(String username, String clientRegistrationId);
    void insert(SocialUser socialUser);

    void update(SocialUser socialUser);

    void deleteByUsernameAndClientRegistrationId(String username, String clientRegistrationId);

}
