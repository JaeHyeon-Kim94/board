package io.oauth.authorizationserver.security.model;

import io.oauth.authorizationserver.web.domain.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Principal implements UserDetails, Serializable {
    private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;
    private User user;
    private final Map<String, Object> attributes;
    public Principal(User user) {
        this.user = user;
        attributes = new HashMap<>();
        attributes.put("fullName", user.getFullname());
        attributes.put("nickname", user.getNickname());
        attributes.put("phone", user.getPhone());
        attributes.put("email", user.getEmail());
        attributes.put("birth", user.getBirth().toString());
    }

    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getRoles();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return getUserId();
    }

    public String getNickname(){
        return user.getNickname();
    }

    public String getUserId(){
        return user.getUserId();
    }

    public String getUserFullName(){
        return user.getFullname();
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
}
