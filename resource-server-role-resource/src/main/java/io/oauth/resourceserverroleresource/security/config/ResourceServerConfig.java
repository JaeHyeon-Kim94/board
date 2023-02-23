package io.oauth.resourceserverroleresource.security.config;

import io.oauth.resourceserverroleresource.security.resolver.CustomJwtIssuerAuthenticationManagerResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.access.vote.RoleHierarchyVoter;
import org.springframework.security.authentication.AuthenticationManagerResolver;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Configuration
public class ResourceServerConfig {

    private final JwtProperties jwtProperties;

    @Bean
    public AuthenticationManagerResolver<HttpServletRequest> authenticationManagerResolver(){
        return new CustomJwtIssuerAuthenticationManagerResolver(jwtProperties.getTrustedIssuerUri());
    }

    @Bean
    public RoleHierarchyImpl roleHierarchy(){
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        return roleHierarchy;
    }

    @Bean
    public RoleHierarchyVoter roleVoter(){
        RoleHierarchyVoter roleHierarchyVoter = new RoleHierarchyVoter(roleHierarchy());
        roleHierarchyVoter.setRolePrefix("ROLE_");
        return roleHierarchyVoter;
    }


    private List<AccessDecisionVoter<?>> getAccessDecisionVoter() {
        return Arrays.asList(roleVoter());
    }

    @Bean
    public AccessDecisionManager affirmitiveBased(){
        return new AffirmativeBased(getAccessDecisionVoter());
    }

}
