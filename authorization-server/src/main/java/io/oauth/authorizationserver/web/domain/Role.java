package io.oauth.authorizationserver.web.domain;

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

    public static Role getDefaultUserRole(){
        Role role = new Role();
        role.setId("U_0000");
        role.setName("ROLE_USER");
        role.setDescription("일반 사용자 권한");
        return role;
    }
}
