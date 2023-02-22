package io.oauth.oauth2authorizationclientserver.security.config;

import io.oauth.oauth2authorizationclientserver.security.common.CustomAuthorityMapper;
import io.oauth.oauth2authorizationclientserver.security.handler.SuccessfulAuthenticationJwtResponseHandler;
import io.oauth.oauth2authorizationclientserver.security.service.CustomOAuth2UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.OAuth2AuthorizationServerMetadataClaimNames;
import org.springframework.security.web.SecurityFilterChain;

@Slf4j
@EnableWebSecurity
public class ClientServerConfig {


    @Value("${token.audience}")
    private String audience;

    @Autowired private CustomOAuth2UserService customOAuthUserService;
    @Autowired private OAuth2AuthorizedClientService oAuth2AuthorizedClientService;
    @Autowired private OAuth2AuthorizedClientRepository oAuth2AuthorizedClientRepository;
    @Autowired private ClientRegistrationRepository clientRegistrationRepository;

    @Autowired private SuccessfulAuthenticationJwtResponseHandler successfulAuthenticationJwtResponseHandler;

    @Autowired private CustomAuthorityMapper customAuthorityMapper;

    @Bean
    public SecurityFilterChain clientServerSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/.well-known/**", "/oauth2/token").permitAll()
                .anyRequest().authenticated();


        http.oauth2Login( oauth2LoginConfigurer -> oauth2LoginConfigurer
                .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig
                        .userService(customOAuthUserService)
                        .userAuthoritiesMapper(customAuthorityMapper)
                )
                .successHandler(successfulAuthenticationJwtResponseHandler)
                .failureHandler((request, response, exception) -> {
                    String message = exception.getMessage();
                    log.error("oauth2 Login Error ====> {} \n, {}", message, exception.getStackTrace());
                    response.sendRedirect(audience+"/login?error="+message);
                })
        );

        http
                .oauth2Client(httpSecurityOAuth2ClientConfigurer -> httpSecurityOAuth2ClientConfigurer
                        .authorizedClientService(oAuth2AuthorizedClientService)
                        .authorizedClientRepository(oAuth2AuthorizedClientRepository)
                        .clientRegistrationRepository(clientRegistrationRepository)
                );

        http
                .sessionManagement( sessionManagementConfigurer -> sessionManagementConfigurer
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer(){
        return web -> web.ignoring()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations())
                .antMatchers(HttpMethod.GET, "/oauth2/jwks")
                ;
    }

}
