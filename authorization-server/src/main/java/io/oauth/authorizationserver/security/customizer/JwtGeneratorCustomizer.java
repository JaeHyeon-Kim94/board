package io.oauth.authorizationserver.security.customizer;

import com.nimbusds.jose.HeaderParameterNames;
import com.nimbusds.jose.JOSEObjectType;
import io.oauth.authorizationserver.security.model.Principal;
import io.oauth.authorizationserver.web.domain.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class JwtGeneratorCustomizer implements OAuth2TokenCustomizer<JwtEncodingContext> {

    @Value("${token.exp}")
    private Long exp;

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
                .expiresAt(now.plus(exp, ChronoUnit.MINUTES))
                .notBefore(now)
                .claims(claim -> {
                    claim.put("fullname", (principal.getUserFullName()));
                    claim.put("nickname", (principal.getNickname()));
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

        context.getHeaders().type(JOSEObjectType.JWT.getType()).build();

        Principal principal = (Principal)context.getPrincipal().getPrincipal();

        List<String> authorities = principal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());


        JwtClaimsSet claimsSet = claims
                .issuer(issuer)
                .subject(String.valueOf(principal.getUserId()))
                .audience(Collections.singletonList(clientId))
                .issuedAt(now)
                .expiresAt(now.plus(exp, ChronoUnit.MINUTES))
                .notBefore(now)
                .claims(claim -> {
                    claim.put("nickname", principal.getNickname());
                    claim.put("authorities", authorities);
                    claim.put("fullname", principal.getUserFullName());
                })
                .build();
    }
}
