package io.oauth.oauth2authorizationclientserver.security.handler;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWK;
import io.oauth.oauth2authorizationclientserver.security.model.PrincipalDetails;
import io.oauth.oauth2authorizationclientserver.security.signer.RSASecuritySigner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Slf4j
@Component
public class SuccessfulAuthenticationJwtResponseHandler implements AuthenticationSuccessHandler {

    @Value("${token.audience}")
    String audience;

    //Social 로그인 성공 후 Id, Access, Refresh Token 발급 성공시 토큰 응답하는 Handler.
    //
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken)authentication;
        String authorizedClientRegistrationId = token.getAuthorizedClientRegistrationId();
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        String idToken = principal.getIdToken().getTokenValue();
        String accessToken = principal.getAccessToken().getTokenValue();
        String refreshToken = principal.getRefreshToken().getTokenValue();

        if(!StringUtils.hasText(idToken)
                ||!StringUtils.hasText(accessToken)
                ||!StringUtils.hasText(refreshToken)){
            response.sendRedirect(audience+"/error");
        }

        StringBuilder sb = new StringBuilder();
        sb
            .append(audience)
                .append("/token?id-token=")
                    .append(idToken)
                .append("&refresh-token=")
                    .append(refreshToken)
                .append("&access-token=")
                    .append(accessToken)
                .append("&token-type=Bearer")
                .append("&reg-id=")
                    .append(token.getAuthorizedClientRegistrationId());

        response.sendRedirect(sb.toString());
    }
}
