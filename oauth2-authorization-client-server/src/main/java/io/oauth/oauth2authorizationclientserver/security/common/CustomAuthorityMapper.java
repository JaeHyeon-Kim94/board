package io.oauth.oauth2authorizationclientserver.security.common;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashSet;

@Component
public class CustomAuthorityMapper implements GrantedAuthoritiesMapper {
    private static final String DEFAULT_PREFIX = "ROLE_";

    @Override
    public Collection<? extends GrantedAuthority> mapAuthorities(Collection<? extends GrantedAuthority> authorities) {

        HashSet<GrantedAuthority> mapped = new HashSet<>(authorities.size());
        for (GrantedAuthority authority : authorities) {
            if(mapAuthority(authority.getAuthority()) != null){
                mapped.add(mapAuthority(authority.getAuthority()));
            }
        }
        return mapped;
    }

    private GrantedAuthority mapAuthority(String name) {
        if(name.lastIndexOf(".") > 0){
            int index = name.lastIndexOf(".");
            name = "SCOPE_" + name.substring(index + 1);
        }

        if(!name.startsWith(DEFAULT_PREFIX)){
            name = DEFAULT_PREFIX + name;
        }
        return new SimpleGrantedAuthority(name);
    }
}
