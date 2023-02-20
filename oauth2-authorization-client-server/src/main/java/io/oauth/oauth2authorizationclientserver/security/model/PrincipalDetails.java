package io.oauth.oauth2authorizationclientserver.security.model;

import io.oauth.oauth2authorizationclientserver.web.domain.SocialUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class PrincipalDetails implements OAuth2User, UserDetails {

    private SocialUser socialUser;
    private Map<String, Object> attributes = new HashMap<>();

    private OAuth2AccessToken accessToken;
    private OAuth2RefreshToken refreshToken;
    private OidcIdToken idToken;

    public PrincipalDetails(SocialUser socialUser, Map<String, Object> attributes, String registrationId) {
        this.socialUser = socialUser;
        this.attributes = setAttributes(attributes, registrationId);
    }

    private Map<String, Object> setAttributes(Map<String, Object> attributes, String registrationId) {
        if(registrationId.equals("naver")){
            return (Map<String, Object>)attributes.get("response");
        }
        return attributes;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return socialUser.getSocialUserId();
    }

    @Override
    public String getName() {
        return socialUser.getFullName();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return socialUser.getRoles();
    }

    public OAuth2AccessToken getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(OAuth2AccessToken accessToken) {
        this.accessToken = accessToken;
    }

    public OAuth2RefreshToken getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(OAuth2RefreshToken refreshToken) {
        this.refreshToken = refreshToken;
    }

    public OidcIdToken getIdToken() {
        return idToken;
    }

    public void setIdToken(OidcIdToken idToken) {
        this.idToken = idToken;
    }
}
