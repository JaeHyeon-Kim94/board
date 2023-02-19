package io.oauth.oauth2authorizationclientserver.security.config;


import io.oauth.oauth2authorizationclientserver.security.config.propertiesconfig.JwtProperties;
import io.oauth.oauth2authorizationclientserver.security.service.CustomOAuth2UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.web.SecurityFilterChain;

@Slf4j
@EnableWebSecurity
public class ClientServerConfig {

    @Autowired JwtProperties jwtProperties;

    @Autowired
    private AuthenticationConfiguration authConfiguration;

    @Autowired private CustomOAuth2UserService customOAuthUserService;
    @Autowired private OAuth2AuthorizedClientService oAuth2AuthorizedClientService;
    @Autowired private OAuth2AuthorizedClientRepository oAuth2AuthorizedClientRepository;
    @Autowired private ClientRegistrationRepository clientRegistrationRepository;

    @Bean
    public SecurityFilterChain clientServerSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests().anyRequest().permitAll();


        http.oauth2Login( oauth2LoginConfigurer -> oauth2LoginConfigurer
                .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig
                        .userService(customOAuthUserService)
                )
                .successHandler((request, response, authentication) -> {

                })
        );

        http
                .oauth2Client(httpSecurityOAuth2ClientConfigurer -> httpSecurityOAuth2ClientConfigurer
                        .authorizedClientService(oAuth2AuthorizedClientService)
                        .authorizedClientRepository(oAuth2AuthorizedClientRepository)
                        .clientRegistrationRepository(clientRegistrationRepository)
                );

        return http.build();
    }

}
