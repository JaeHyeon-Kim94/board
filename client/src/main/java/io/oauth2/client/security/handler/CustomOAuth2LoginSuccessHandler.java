package io.oauth2.client.security.handler;

import io.oauth2.client.security.config.propertiesconfig.JwtProperties;
import io.oauth2.client.security.utils.CookieUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.temporal.ChronoUnit;
import java.util.Base64;

@RequiredArgsConstructor
@Component
public class CustomOAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtProperties jwtProperties;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        if(!(authentication.getPrincipal() instanceof OidcUser)){
            throw new RuntimeException("authentication principal only OidcUser");
        }
        OAuth2AuthenticationToken authenticationToken = (OAuth2AuthenticationToken)authentication;
        OidcUser principal = (OidcUser) authentication.getPrincipal();
        OidcIdToken idToken = principal.getIdToken();


        //encode registration id to Base64
        String encodedRegId = Base64.getEncoder()
                .encodeToString(authenticationToken.getAuthorizedClientRegistrationId().getBytes());



        CookieUtils.addCookie(response, "idt", idToken.getTokenValue(), jwtProperties.getCookieMaxAge()*60);
        CookieUtils.addCookie(response, "reg", encodedRegId, jwtProperties.getCookieMaxAge()*60);


        this.getRedirectStrategy().sendRedirect(request, response, "/");
    }

    private void addCookie(String key, String value, HttpServletResponse response){
        Cookie cookie = new Cookie(key, value);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(60*60*24);
        cookie.setPath("/");

        response.addCookie(cookie);
    }
}
