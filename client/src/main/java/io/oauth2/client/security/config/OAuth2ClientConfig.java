package io.oauth2.client.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.oauth2.client.security.common.CustomAuthorityMapper;
import io.oauth2.client.security.config.propertiesconfig.JwtProperties;
import io.oauth2.client.security.entrypoint.OAuth2LoginAuthenticationEntrypoint;
import io.oauth2.client.security.handler.CustomOAuth2LoginSuccessHandler;
import io.oauth2.client.security.provider.CustomRefreshTokenOAuth2AuthorizedClientProvider;
import io.oauth2.client.security.repository.HttpCookieOAuth2AuthorizationRequestRepository;
import io.oauth2.client.security.resolver.CustomeBearerTokenResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.AuthenticationManagerResolver;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.*;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.server.resource.authentication.JwtIssuerAuthenticationManagerResolver;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

@Configuration
public class OAuth2ClientConfig {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    JwtProperties jwtProperties;


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
        return new JwtIssuerAuthenticationManagerResolver(jwtProperties.getTrustedIssuerUri());
    }

    @Bean
    public CustomRefreshTokenOAuth2AuthorizedClientProvider customRefreshTokenOAuth2AuthorizedClientProvider(){
        return new CustomRefreshTokenOAuth2AuthorizedClientProvider();
    }

    @Bean
    public OAuth2LoginAuthenticationEntrypoint oAuth2LoginAuthenticationEntrypoint(OAuth2AuthorizedClientService clientService, OAuth2AuthorizedClientManager oAuth2AuthorizedClientManager) {
        return new OAuth2LoginAuthenticationEntrypoint(clientService, oAuth2AuthorizedClientManager);
    }

    @Bean
    public CustomAuthorityMapper customAuthorityMapper(){
        return new CustomAuthorityMapper();
    }

    @Bean
    public CustomeBearerTokenResolver customeBearerTokenResolver(OAuth2AuthorizedClientService oAuth2AuthorizedClientService){
        return new CustomeBearerTokenResolver(oAuth2AuthorizedClientService);
    }

}
