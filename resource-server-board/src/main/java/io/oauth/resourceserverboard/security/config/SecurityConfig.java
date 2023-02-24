package io.oauth.resourceserverboard.security.config;

import io.oauth.resourceserverboard.security.filter.PermitAllFilter;
import io.oauth.resourceserverboard.security.metadatasource.UrlFilterInvocationSecurityMetadataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.authentication.AuthenticationManagerResolver;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    private static final String[] PERMIT_ALL_PATTERN = new String[]{ "/posts/**" };
    private final AuthenticationManagerResolver<HttpServletRequest> authenticationManagerResolver;
    private final UrlFilterInvocationSecurityMetadataSource urlFilterInvocationSecurityMetadataSource;
    private final AccessDecisionManager affirmitiveBased;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf().disable()
                .formLogin().disable()
                .authorizeRequests()
                        .anyRequest().authenticated();

        http
                .oauth2ResourceServer(
                        resourceServerConfigurer -> resourceServerConfigurer
                                .authenticationManagerResolver(authenticationManagerResolver)
                );

        //Authorization Config(PermitAll)
        http
                .addFilterAt(permitAllFilter(), FilterSecurityInterceptor.class);


        http
                .exceptionHandling(httpSecurityExceptionHandlingConfigurer -> httpSecurityExceptionHandlingConfigurer
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.sendError(HttpServletResponse.SC_FORBIDDEN, accessDeniedException.getMessage());
                        })
                );


        return http.build();
    }

    @Bean
    public PermitAllFilter permitAllFilter() throws Exception {
        PermitAllFilter permitAllFilter = new PermitAllFilter(authenticationManagerResolver, PERMIT_ALL_PATTERN);
        permitAllFilter.setSecurityMetadataSource(urlFilterInvocationSecurityMetadataSource);
        permitAllFilter.setAccessDecisionManager(affirmitiveBased);
        return permitAllFilter;
    }
}
