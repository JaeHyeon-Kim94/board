package io.oauth.oauth2authorizationclientserver.security.repository.socialuser;

import io.oauth.oauth2authorizationclientserver.web.domain.SocialUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class SocialUserRepositoryMybatis implements SocialUserRepository {

    private final SocialUserMapper socialUserMapper;

    @Override
    public SocialUser findByUsernameAndClientRegistrationId(String username, String clientRegistrationId) {
        return socialUserMapper.findByUsernameAndClientRegistrationId(username, clientRegistrationId);
    }

    @Override
    public SocialUser insert(SocialUser socialUser) {
        socialUserMapper.insert(socialUser);
        return socialUser;
    }

    @Override
    public void update(SocialUser socialUser) {
        socialUserMapper.update(socialUser);
    }

    @Override
    public void deleteByUsernameAndClientRegistrationId(String username, String clientRegistrationId) {
        socialUserMapper.deleteByUsernameAndClientRegistrationId(username, clientRegistrationId);
    }
}
