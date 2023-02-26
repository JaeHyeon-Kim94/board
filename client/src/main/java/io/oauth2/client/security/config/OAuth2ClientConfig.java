package io.oauth2.client.security.config;

import io.oauth2.client.resource.ResourceService;
import io.oauth2.client.security.common.CustomAuthorityMapper;
import io.oauth2.client.security.config.propertiesconfig.JwtProperties;
import io.oauth2.client.security.entrypoint.OAuth2LoginAuthenticationEntrypoint;
import io.oauth2.client.security.factory.UrlResourcesMapFactoryBean;
import io.oauth2.client.security.metadatasource.UrlFilterInvocationSecurityMetadataSource;
import io.oauth2.client.security.provider.CustomRefreshTokenOAuth2AuthorizedClientProvider;
import io.oauth2.client.security.repository.HttpCookieOAuth2AuthorizationRequestRepository;
import io.oauth2.client.security.resolver.CustomJwtIssuerAuthenticationManagerResolver;
import io.oauth2.client.security.resolver.CustomeBearerTokenResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.access.vote.RoleHierarchyVoter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationManagerResolver;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.*;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.server.resource.authentication.JwtIssuerAuthenticationManagerResolver;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;

@RequiredArgsConstructor
@Configuration
public class OAuth2ClientConfig {
    private final JdbcTemplate jdbcTemplate;
    private final JwtProperties jwtProperties;

    private final ResourceService securityResourceService;
    private final UrlResourcesMapFactoryBean urlResourceMapFactoryBean;


    @Bean
    public DefaultOAuth2AuthorizedClientManager oAuth2AuthorizedClientManager(ClientRegistrationRepository clientRegistrationRepository,
                                                                        OAuth2AuthorizedClientRepository auth2AuthorizedClientRepository){
        OAuth2AuthorizedClientProvider authorizedClientProvider = OAuth2AuthorizedClientProviderBuilder.builder()
                .authorizationCode()
                .provider(customRefreshTokenOAuth2AuthorizedClientProvider())
                .build();

        DefaultOAuth2AuthorizedClientManager defaultOAuth2AuthorizedClientManager
                = new DefaultOAuth2AuthorizedClientManager(clientRegistrationRepository, auth2AuthorizedClientRepository);

        defaultOAuth2AuthorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);

        defaultOAuth2AuthorizedClientManager.setAuthorizationSuccessHandler((authorizedClient, principal, attributes) -> {
            auth2AuthorizedClientRepository
                    .saveAuthorizedClient(authorizedClient, principal,
                            (HttpServletRequest) attributes.get(HttpServletRequest.class.getName()),
                            (HttpServletResponse) attributes.get(HttpServletResponse.class.getName()));


        });


        return defaultOAuth2AuthorizedClientManager;
    }


    @Bean
    public OAuth2AuthorizedClientRepository oAuth2AuthorizedClientRepository(){
        return new AuthenticatedPrincipalOAuth2AuthorizedClientRepository(oAuth2AuthorizedClientService(null));
    }

    @Bean
    public AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository(){
        return new HttpCookieOAuth2AuthorizationRequestRepository();
    }

    @Bean
    public OAuth2AuthorizedClientService oAuth2AuthorizedClientService(ClientRegistrationRepository clientRegistrationRepository){
        return new JdbcOAuth2AuthorizedClientService(jdbcTemplate, clientRegistrationRepository);
    }

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

    @Bean
    public CustomRefreshTokenOAuth2AuthorizedClientProvider customRefreshTokenOAuth2AuthorizedClientProvider(){
        return new CustomRefreshTokenOAuth2AuthorizedClientProvider();
    }

    @Bean
    public OAuth2LoginAuthenticationEntrypoint oAuth2LoginAuthenticationEntrypoint(OAuth2AuthorizedClientService clientService, OAuth2AuthorizedClientManager oAuth2AuthorizedClientManager) {
        return new OAuth2LoginAuthenticationEntrypoint(clientService, oAuth2AuthorizedClientManager, jwtProperties);
    }

    @Bean
    public CustomAuthorityMapper customAuthorityMapper(){
        return new CustomAuthorityMapper();
    }

    @Bean
    public CustomeBearerTokenResolver customeBearerTokenResolver(OAuth2AuthorizedClientService oAuth2AuthorizedClientService){
        return new CustomeBearerTokenResolver(jwtProperties, oAuth2AuthorizedClientService);
    }

}
