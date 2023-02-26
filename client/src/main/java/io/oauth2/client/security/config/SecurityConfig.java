package io.oauth2.client.security.config;

import io.oauth2.client.security.common.CustomAuthorityMapper;
import io.oauth2.client.security.entrypoint.OAuth2LoginAuthenticationEntrypoint;
import io.oauth2.client.security.filter.PermitAllFilter;
import io.oauth2.client.security.filter.SocialLoginTokenResponseProcessingFilter;
import io.oauth2.client.security.handler.CustomOAuth2LoginSuccessHandler;
import io.oauth2.client.security.handler.JwtLogoutHandler;
import io.oauth2.client.security.metadatasource.UrlFilterInvocationSecurityMetadataSource;
import io.oauth2.client.security.resolver.CustomeBearerTokenResolver;
import io.oauth2.client.security.service.CustomOAuth2UserService;
import io.oauth2.client.security.service.CustomOidcUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.authentication.AuthenticationManagerResolver;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.http.HttpServletRequest;
@Slf4j
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    private static final String[] PERMIT_ALL_PATTERN
            = new String[]{
                    "/js/**", "/css/**", "/", "/login", "/boards", "/post", "/api/oauth/**"};
    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomOidcUserService customOidcUserService;
    private final AuthenticationManagerResolver<HttpServletRequest> authenticationManagerResolver;
    private final OAuth2LoginAuthenticationEntrypoint oAuth2LoginAuthenticationEntrypoint;
    private final CustomeBearerTokenResolver customeBearerTokenResolver;
    private final CustomOAuth2LoginSuccessHandler customOAuth2LoginSuccessHandler;
    private final CustomAuthorityMapper customAuthorityMapper;
    private final AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository;
    private final JwtLogoutHandler jwtLogoutHandler;
    private final SocialLoginTokenResponseProcessingFilter socialLoginTokenResponseProcessingFilter;
    private final UrlFilterInvocationSecurityMetadataSource urlFilterInvocationSecurityMetadataSource;
    private final AccessDecisionManager affirmitiveBased;

    @Bean
    public SecurityFilterChain oauth2ClientSecurityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf().disable()
                .formLogin().disable()
                .authorizeRequests()
                .anyRequest().authenticated();


        http.oauth2Login( oauth2LoginConfigurer -> oauth2LoginConfigurer
                .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig
                        .userService(customOAuth2UserService)
                        .oidcUserService(customOidcUserService)
                        .userAuthoritiesMapper(customAuthorityMapper))
                .authorizationEndpoint(authorizationEndpointConfig -> authorizationEndpointConfig
                        .authorizationRequestRepository(authorizationRequestRepository)
                )
                .successHandler(customOAuth2LoginSuccessHandler)
                .loginPage("/login")
        );

        http
                .oauth2Client( oauth2ClientConfigurer -> {
                    //oauth2ClientConfigurer
                });

        http
                .oauth2ResourceServer(resourceServerConfigurer -> resourceServerConfigurer
                        .authenticationManagerResolver(authenticationManagerResolver)
                        .bearerTokenResolver(customeBearerTokenResolver)
                        .authenticationEntryPoint(oAuth2LoginAuthenticationEntrypoint)
                );

        http
                .logout(logoutConfigurer -> logoutConfigurer
                        .logoutUrl("/logout")
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout", HttpMethod.GET.name()))
                        .logoutSuccessUrl("/")
                        .addLogoutHandler(jwtLogoutHandler)
                );

        http
                .exceptionHandling( exceptionHandlingConfigurer ->
                        exceptionHandlingConfigurer
                            .accessDeniedHandler((request, response, accessDeniedException)
                                    -> response.sendRedirect("/"))
                            .authenticationEntryPoint((request, response, authException)
                                    -> response.sendRedirect("/")));

        http.sessionManagement( sessionConfigurer -> sessionConfigurer
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        http
                .addFilterBefore(socialLoginTokenResponseProcessingFilter, OAuth2AuthorizationRequestRedirectFilter.class);

        http
                .addFilterAt(permitAllFilter(), FilterSecurityInterceptor.class);

        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer(){
        return web -> web.ignoring()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    @Bean
    public PermitAllFilter permitAllFilter() throws Exception {
        PermitAllFilter permitAllFilter = new PermitAllFilter(authenticationManagerResolver, PERMIT_ALL_PATTERN);
        permitAllFilter.setSecurityMetadataSource(urlFilterInvocationSecurityMetadataSource);
        permitAllFilter.setAccessDecisionManager(affirmitiveBased);
        return permitAllFilter;
    }
}

