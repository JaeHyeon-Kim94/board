package io.oauth.resourceserverrolesresources.security.config;

import io.oauth.resourceserverrolesresources.security.factory.UrlResourcesMapFactoryBean;
import io.oauth.resourceserverrolesresources.security.metadatasource.UrlFilterInvocationSecurityMetadataSource;
import io.oauth.resourceserverrolesresources.security.resolver.CustomJwtIssuerAuthenticationManagerResolver;
import io.oauth.resourceserverrolesresources.service.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.access.vote.RoleHierarchyVoter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationManagerResolver;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;

@RequiredArgsConstructor
@Configuration
public class ResourceServerConfig {
    private final JwtProperties jwtProperties;
    private final ResourceService securityResourceService;
    private final UrlResourcesMapFactoryBean urlResourceMapFactoryBean;

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



    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public UrlFilterInvocationSecurityMetadataSource urlFilterInvocationSecurityMetadataSource() throws Exception {
        return new UrlFilterInvocationSecurityMetadataSource(urlResourceMapFactoryBean.getObject(), securityResourceService);
    }

    @Bean
    public AccessDecisionManager affirmitiveBased(){
        return new AffirmativeBased(Collections.singletonList(roleVoter()));
    }

}
