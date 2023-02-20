package io.oauth.oauth2authorizationclientserver.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.JdbcOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.AuthenticatedPrincipalOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;

import javax.sql.DataSource;

@Configuration
public class SecurityConfig {

    @Autowired ClientRegistrationRepository clientRegistrationRepository;

    @Bean
    public OAuth2AuthorizedClientRepository oAuth2AuthorizedClientRepository(){
        return new AuthenticatedPrincipalOAuth2AuthorizedClientRepository(authorizedClientService());
    }

    @Bean
    public OAuth2AuthorizedClientService authorizedClientService() {
        return new JdbcOAuth2AuthorizedClientService(jdbcTemplate(null), clientRegistrationRepository);
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource){
        return new JdbcTemplate(dataSource);
    }

}
