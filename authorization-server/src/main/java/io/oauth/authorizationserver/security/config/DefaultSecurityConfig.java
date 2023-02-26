package io.oauth.authorizationserver.security.config;

import io.oauth.authorizationserver.repository.UserRepository;
import io.oauth.authorizationserver.security.provider.FormUserAuthenticationProvider;
import io.oauth.authorizationserver.security.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import javax.servlet.http.HttpSession;

@RequiredArgsConstructor
@EnableWebSecurity
public class DefaultSecurityConfig {

    private final ObjectFactory<HttpSession> httpSessionFactory;

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer(){
        return web -> {
            web.ignoring()
                    .requestMatchers(PathRequest.toStaticResources().atCommonLocations())
            ;
        };
    }

    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {



        http.authorizeRequests(request ->
                request
                        .antMatchers("/", "/error", "/login", "/join", "/members/**/check-duplicated").permitAll()
                        .anyRequest().authenticated());

        http.formLogin(
                formLoginConfigurer -> formLoginConfigurer
                        .usernameParameter("userId")
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
        );

        http.authenticationProvider(new FormUserAuthenticationProvider(passwordEncoder(), userDetailsService(null), httpSessionFactory));

        //logout config
        http
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login")
                .deleteCookies("JSESSIONID", "remember-me");

        //Session Management
        http
                .sessionManagement()
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false)
                .and()
                .invalidSessionUrl("/login")
                .sessionFixation().none();


        return http.build();
    }


    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository){
        return new CustomUserDetailsService(userRepository);
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

}
