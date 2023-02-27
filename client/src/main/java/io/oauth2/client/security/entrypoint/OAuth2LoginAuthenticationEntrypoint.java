package io.oauth2.client.security.entrypoint;

import io.oauth2.client.security.config.propertiesconfig.JwtProperties;
import io.oauth2.client.security.model.CustomOAuth2AuthorizedClient;
import io.oauth2.client.security.utils.CookieUtils;
import io.oauth2.client.security.utils.JwtUtils;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.jwt.JwtValidationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Base64;
import java.util.Map;

@Component
public class OAuth2LoginAuthenticationEntrypoint implements AuthenticationEntryPoint {


    private final OAuth2AuthorizedClientService oAuth2AuthorizedClientService;
    private final OAuth2AuthorizedClientManager oauth2AuthorizedClientManager;
    private final JwtProperties jwtProperties;

    public OAuth2LoginAuthenticationEntrypoint(OAuth2AuthorizedClientService oAuth2AuthorizedClientService, OAuth2AuthorizedClientManager oauth2AuthorizedClientManager, JwtProperties jwtProperties) {
        this.oAuth2AuthorizedClientService = oAuth2AuthorizedClientService;
        this.oauth2AuthorizedClientManager = oauth2AuthorizedClientManager;
        this.jwtProperties = jwtProperties;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        Throwable cause = authException.getCause();
        if (cause instanceof JwtValidationException) {
            getNewAccessToken(request, response);
        } else {
            CookieUtils.deleteCookies(request, response);
            response.sendRedirect("/");
        }
    }

    private void getNewAccessToken(HttpServletRequest request, HttpServletResponse response) throws IOException {

        Cookie[] cookies = request.getCookies();
        if(cookies == null){
            response.sendRedirect("/");
            return;
        }

        String idToken = null;
        String regId = null;

        String idTokenCookieName = jwtProperties.getIdTokenCookieName();
        String regIdCookieName = jwtProperties.getRegIdCookieName();
        int cookieMaxAge = jwtProperties.getCookieMaxAge();


        for (Cookie cookie : cookies) {
            if(cookie.getName().equals(regIdCookieName)){
                regId = cookie.getValue();
            }else if(cookie.getName().equals(idTokenCookieName)){
                idToken = cookie.getValue();
            }
        }

        if(!StringUtils.hasText(regId) || !StringUtils.hasText(idToken)){
            response.sendRedirect("/login");
            return;
        }

        Map<String, Object> claims = JwtUtils.getClaims(idToken);

        if(claims.isEmpty()){
            response.sendRedirect("/login");
            return;
        }

        regId = new String(Base64.getDecoder().decode(regId));

        OAuth2AuthorizedClient authorizedClient
                = oAuth2AuthorizedClientService.loadAuthorizedClient(regId, (String) claims.get("sub"));


        ClientRegistration clientRegistration = ClientRegistration.withClientRegistration(authorizedClient.getClientRegistration())
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .build();

        OAuth2AuthorizedClient oAuth2AuthorizedClient = new OAuth2AuthorizedClient(clientRegistration
                , authorizedClient.getPrincipalName()
                , authorizedClient.getAccessToken()
                , authorizedClient.getRefreshToken());

        OAuth2AuthorizeRequest oAuth2AuthorizeRequest = OAuth2AuthorizeRequest.withAuthorizedClient(oAuth2AuthorizedClient)
                .principal(authorizedClient.getPrincipalName())
                .attribute(HttpServletRequest.class.getName(), request)
                .attribute(HttpServletResponse.class.getName(), response)
                .build();

        OAuth2AuthorizedClient authorize = null;

        try{
            authorize = oauth2AuthorizedClientManager.authorize(oAuth2AuthorizeRequest);
        } catch (Exception ex){
            CookieUtils.deleteCookies(request, response);
            response.sendRedirect("/login");
            return;
        }


        if(authorize == null){
            CookieUtils.deleteCookies(request, response);
            response.sendRedirect("/login");
            return;
        }

        CustomOAuth2AuthorizedClient reAuthorizedClient = (CustomOAuth2AuthorizedClient)authorize;

        OidcIdToken refreshedIdToken = reAuthorizedClient.getIdToken();

        CookieUtils.addCookie(response, idTokenCookieName, refreshedIdToken.getTokenValue(), cookieMaxAge);

        regId = reAuthorizedClient.getClientRegistration().getRegistrationId();
        CookieUtils.addCookie(response, regIdCookieName, Base64.getEncoder().encodeToString(regId.getBytes()), cookieMaxAge);

        response.sendRedirect("/");
    }
}
