package io.oauth.authorizationserver.security.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.authorization.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.server.authorization.InMemoryOAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.InMemoryOAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.ProviderSettings;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenGenerator;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletResponse;
@RequiredArgsConstructor
@Configuration
public class OAuth2AuthorizationConfig {

    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
    private final JdbcTemplate jdbcTemplate;
    private final OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator;

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain oauthSecurityFilterChain(HttpSecurity http) throws Exception {

        http.authorizeRequests().requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll();

        //authorization server ?????? ?????? ??? customize
        applyDefaultAuthorizationServerConfigure(http);

        return http.build();
    }

    private void applyOAuth2ClientServerConfigurer(HttpSecurity http) {

    }

    private void applyDefaultAuthorizationServerConfigure(HttpSecurity http) throws Exception {
        //Default Config
        OAuth2AuthorizationServerConfigurer<HttpSecurity> oAuth2AuthorizationServerConfigurer
                = new OAuth2AuthorizationServerConfigurer<>();
        RequestMatcher endPointsMatcher = oAuth2AuthorizationServerConfigurer.getEndpointsMatcher();
        http
                .requestMatcher(endPointsMatcher)
                .authorizeRequests( requests ->
                        requests.anyRequest().authenticated()
                )
                .csrf( csrf -> csrf.ignoringRequestMatchers(endPointsMatcher))
                .apply(oAuth2AuthorizationServerConfigurer);

//==================================================================================================================
//        customizing
        customizeConfig(oAuth2AuthorizationServerConfigurer);
//==================================================================================================================

        http
                .exceptionHandling( exceptions ->
                        exceptions
                                .accessDeniedHandler((request, response, accessDeniedException) -> {
                                    String s = "AccessDeniedHandler : Access Denied..." + accessDeniedException.getMessage();
                                    response.sendError(HttpServletResponse.SC_FORBIDDEN, s);
                                })
                                .authenticationEntryPoint((request, response, authException) -> {
                                    this.redirectStrategy.sendRedirect(request, response, "/login");
                                })
                );
    }

    private void customizeConfig(OAuth2AuthorizationServerConfigurer<HttpSecurity> oAuth2AuthorizationServerConfigurer) {
        oAuth2AuthorizationServerConfigurer
                //endpoint, issuer uri, jwkset uri config.
                .providerSettings(providerSettings())

                //????????? ?????????????????? ??????????????? repository.
                .registeredClientRepository(registeredClientRepository())

                //OAuth2Authorization ?????? ??????, ?????? ??????.
                //??? ????????? AuthorizationCode Grant Type??? ?????? Resource Owner??? ?????? ?????? ?????? ????????? ???????????? ?????? ??????.
                .authorizationService(oAuth2AuthorizationService())
                .authorizationConsentService(oAuth2AuthorizationConsentService())

                //token generator
                .tokenGenerator(tokenGenerator)

                //endpoint config
                //1. authorization endpoint
                //authorization code ?????? ==> /oauth2/authorize, GET, /oauth2/authorize, POST
                //consent ??????            ==> /oauth2/authorize, POST
                .authorizationEndpoint(authorizationEndpointConfigurer -> { })

                //2. Client Authentication Customize
                .clientAuthentication( clientAuthenticationConfigurer -> { })

                //3. token Endpoint Customize
                .tokenEndpoint( tokenEndpointConfigurer -> {})
                .tokenIntrospectionEndpoint( tokenIntrospectionEndpointConfigurer -> {})
                .tokenRevocationEndpoint( tokenRevocationEndpointConfigurer -> {})

                //4. OIDC endpoint Customize
                .oidc( oidcConfigurer -> {});
    }

    //==================================================================================================================

    private ProviderSettings providerSettings() {
        return ProviderSettings.builder()
                .issuer("http://127.0.0.1:9000")
                .authorizationEndpoint("/oauth2/authorize")
                .tokenEndpoint("/oauth2/token")
                .jwkSetEndpoint("/oauth2/jwks")
                .tokenRevocationEndpoint("/oauth2/revoke")
                .tokenIntrospectionEndpoint("/oauth2/introspect")
                .oidcClientRegistrationEndpoint("/connect/register")
                .oidcUserInfoEndpoint("/userinfo")
                .build();
    }

    //????????? OAuth Client??? ???????????? ??????????????? ??????.
    @Bean
    public RegisteredClientRepository registeredClientRepository(){
        JdbcRegisteredClientRepository repository = new JdbcRegisteredClientRepository(jdbcTemplate);
        return repository;
    }

    @Bean
    public OAuth2AuthorizationService oAuth2AuthorizationService(){
        return new InMemoryOAuth2AuthorizationService();
        //new JdbcOAuth2AuthorizationService(jdbcTemplate, registeredClientRepository());
    }

    @Bean
    public OAuth2AuthorizationConsentService oAuth2AuthorizationConsentService(){
        return new InMemoryOAuth2AuthorizationConsentService();
        //new JdbcOAuth2AuthorizationConsentService(jdbcTemplate, registeredClientRepository());
    }

}
