package io.oauth.oauth2authorizationclientserver.web.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

@Getter
@Setter
public class Role implements GrantedAuthority {

    public Role() {
    }

    public Role(String name) {
        this.name = name;
    }

    private String id;
    private String description;
    private String name;

    @Override
    public String getAuthority() {
        return this.name;
    }
}
