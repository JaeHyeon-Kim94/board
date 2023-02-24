package io.oauth.resourceserverboard.security.converter;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

import java.util.*;

public class CustomJwtAuthenticationConverter extends JwtAuthenticationConverter {

    private static final String DEFAULT_ROLE_PREFIX = "ROLE_";

    @Override
    protected Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {

        Map<String, Object> claims = jwt.getClaims();

        ArrayList<String> authorities = (ArrayList) claims.get("authorities");
        Set<GrantedAuthority> roles = new HashSet<>();
        for (String authority : authorities) {
            if(!authority.contains(DEFAULT_ROLE_PREFIX)){
                authority = DEFAULT_ROLE_PREFIX + authority;
            }
            roles.add(new SimpleGrantedAuthority(authority));
        }

        return roles;
    }
}
