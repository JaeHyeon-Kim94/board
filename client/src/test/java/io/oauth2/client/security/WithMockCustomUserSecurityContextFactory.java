package io.oauth2.client.security;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import static org.springframework.security.core.authority.AuthorityUtils.createAuthorityList;

public class WithMockCustomUserSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUser>{
    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser annotation) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Jwt jwt = Jwt.withTokenValue("token")
                .claim("sub", annotation.userId())
                .claim("authorities", annotation.role())
                .header("alg", "none")
                .header("typ", "JWT")
                .build();

        JwtAuthenticationToken token = new JwtAuthenticationToken(jwt, createAuthorityList(annotation.role()));

        context.setAuthentication(token);

        return context;
    }
}
