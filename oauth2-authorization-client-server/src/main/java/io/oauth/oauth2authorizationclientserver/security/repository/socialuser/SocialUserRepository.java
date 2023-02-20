package io.oauth.oauth2authorizationclientserver.security.repository.socialuser;


import io.oauth.oauth2authorizationclientserver.web.domain.SocialUser;

public interface SocialUserRepository {

    SocialUser findByUsernameAndClientRegistrationId(String username, String clientRegistrationId);
    SocialUser insert(SocialUser socialUser);

    void update(SocialUser socialUser);

    void deleteByUsernameAndClientRegistrationId(String username, String clientRegistrationId);

}
