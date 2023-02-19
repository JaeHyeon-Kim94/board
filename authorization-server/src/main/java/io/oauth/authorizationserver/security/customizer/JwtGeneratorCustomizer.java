package io.oauth.authorizationserver.security.customizer;

import io.oauth.authorizationserver.security.model.Principal;
import org.springframework.security.oauth2.core.OAuth2TokenType;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;

public class JwtGeneratorCustomizer implements OAuth2TokenCustomizer<JwtEncodingContext> {
    @Override
    public void customize(JwtEncodingContext context) {

        OAuth2TokenType tokenType = context.getTokenType();
        if(OAuth2TokenType.ACCESS_TOKEN.equals(context.getTokenType())){
            customizeAccessToken(context);
        }else if(OidcParameterNames.ID_TOKEN.equals(context.getTokenType().getValue())){
            customizeOidcIdToken(context);
        } else {
            throw new RuntimeException("Jwt Generate 도중 오류 발생 ==> 잘못된 토큰 타입.");
        }
    }

    private void customizeOidcIdToken(JwtEncodingContext context) {

        JwtClaimsSet.Builder claims = context.getClaims();
        RegisteredClient registeredClient = context.getRegisteredClient();
        String clientId = registeredClient.getClientId();
        String issuer = null;
        if (context.getProviderContext() != null) {
            issuer = context.getProviderContext().getIssuer();
        }

        Principal principal = (Principal)context.getPrincipal().getPrincipal();

        context.getHeaders().header("typ", "jwt").build();
        Instant now = Instant.now();
        JwtClaimsSet claimsSet = claims
                .issuer(issuer)
                .subject(String.valueOf(principal.getUserId()))
                .audience(Collections.singletonList(clientId))
                .issuedAt(now)
                .notBefore(now)
                .expiresAt(now.plus(5L, ChronoUnit.SECONDS))
                .claims(claim -> {
                    claim.put("id", ((Principal) context.getPrincipal().getPrincipal()).getUserId());
                })
                .build();


    }

    private void customizeAccessToken(JwtEncodingContext context) {
        JwtClaimsSet.Builder claims = context.getClaims();
        RegisteredClient registeredClient = context.getRegisteredClient();
        String clientId = registeredClient.getClientId();

        String issuer = null;
        if (context.getProviderContext() != null) {
            issuer = context.getProviderContext().getIssuer();
        }

        Instant now = Instant.now();
        Duration accessTokenTimeToLive = registeredClient.getTokenSettings().getAccessTokenTimeToLive();
        long minutes = accessTokenTimeToLive.toMinutes();

        context.getHeaders().header("typ", "jwt").build();

        Principal principal = (Principal)context.getPrincipal().getPrincipal();

        JwtClaimsSet claimsSet = claims
                .issuer(issuer)
                .subject(String.valueOf(principal.getUserId()))
                .audience(Collections.singletonList(clientId))
                .issuedAt(now)
                .notBefore(now)
                .expiresAt(now.plus(5L, ChronoUnit.SECONDS))
                .claims(claim -> {
                    claim.put("id", principal.getUserId());
                    claim.put(OAuth2ParameterNames.SCOPE, context.getAuthorizedScopes());
                    claim.putAll(principal.getAttributes());
                })
                .build();
    }
}
