package io.oauth2.client.security.filter;

import io.oauth2.client.security.config.propertiesconfig.JwtProperties;
import io.oauth2.client.security.resolver.CustomeBearerTokenResolver;
import io.oauth2.client.security.utils.CookieUtils;
import io.oauth2.client.security.utils.JwtUtils;
import org.springframework.http.HttpMethod;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class SocialLoginTokenResponseProcessingFilter extends OncePerRequestFilter {

    private static final RequestMatcher FILTER_REQUEST_MATCHER = new AntPathRequestMatcher("/token*", HttpMethod.GET.name());
    private final JwtProperties jwtProperties;
    private final OAuth2AuthorizedClientService oAuth2AuthorizedClientService;
    private final ClientRegistrationRepository clientRegistrationRepository;

    public SocialLoginTokenResponseProcessingFilter(JwtProperties jwtProperties
            , OAuth2AuthorizedClientService oAuth2AuthorizedClientService
            , ClientRegistrationRepository clientRegistrationRepository) {
        this.jwtProperties = jwtProperties;
        this.oAuth2AuthorizedClientService = oAuth2AuthorizedClientService;
        this.clientRegistrationRepository = clientRegistrationRepository;
    }

    /**
     *  1. oauth2-authorization-client-server?????? ????????? ???????????? ??? ID Token??? ProviderId(registrationId)??? ????????? ?????????.
     *  2. DB??? oauth2-authorized-client ???????????? access token, refresh token??? ?????????.
     *    2-1. access token??? resource server?????? ????????? ?????? ????????????, ???????????? ?????? ????????? ????????? ??????????????? ???????????????.
     *    2-2. refresh token??? ?????? ????????? ?????? ??????????????? ?????? ???????????? ????????????.
     *  3. ????????? ????????? ??????????????? ????????????, redirect?????? ?????? ?????? ??? ?????? ????????? ????????????.
     *    {@link CustomeBearerTokenResolver}
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if(!FILTER_REQUEST_MATCHER.matches(request)){
            filterChain.doFilter(request, response);
            return;
        }

        String attributeNameKey = "sub";
        String registrationId = "simpleOAuth";
        String encodedRegId =Base64.getEncoder().encodeToString(registrationId.getBytes(StandardCharsets.UTF_8));
        //1
        String idTokenStringValue       = request.getParameter("id-token");

        String idTokenCookieName = jwtProperties.getIdTokenCookieName();
        String regIdCookieName = jwtProperties.getRegIdCookieName();
        int cookieMaxAge = jwtProperties.getCookieMaxAge();

        CookieUtils.addCookie(response, idTokenCookieName, idTokenStringValue, cookieMaxAge);
        CookieUtils.addCookie(response, regIdCookieName, encodedRegId, cookieMaxAge);

        String accessTokenStringValue   = request.getParameter("access-token");
        String refreshTokenStringValue  = request.getParameter("refresh-token");
        String tokenType                = request.getParameter("token-type");



        //2
        OAuth2AccessToken accessToken
                = (OAuth2AccessToken)JwtUtils.convertTokenValueStringToOAuth2Token(accessTokenStringValue, OAuth2ParameterNames.ACCESS_TOKEN);
        OAuth2RefreshToken refreshToken = new OAuth2RefreshToken(refreshTokenStringValue, null, null);

        ClientRegistration clientRegistration = ClientRegistration.withRegistrationId(registrationId)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .clientId("none").redirectUri("none").tokenUri("none").authorizationUri("none")
                .issuerUri("http://127.0.0.1:9001")
                .jwkSetUri("http://127.0.0.1:9001/oauth2/jwks")
                .build();
        String principalName = (String)JwtUtils.getClaims(idTokenStringValue).get(attributeNameKey);
        OAuth2AuthorizedClient oAuth2AuthorizedClient
                = new OAuth2AuthorizedClient(clientRegistration, principalName, accessToken, refreshToken);


        OAuth2User user =
            new DefaultOAuth2User(null, JwtUtils.getClaims(accessTokenStringValue), attributeNameKey);

        OAuth2AuthenticationToken authenticationToken
                = new OAuth2AuthenticationToken(user, null, clientRegistration.getRegistrationId());

            oAuth2AuthorizedClientService.saveAuthorizedClient(oAuth2AuthorizedClient, authenticationToken);


        response.sendRedirect("/");
    }
}
