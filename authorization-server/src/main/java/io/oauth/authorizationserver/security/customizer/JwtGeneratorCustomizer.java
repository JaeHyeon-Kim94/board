package io.oauth.authorizationserver.security.customizer;

import io.oauth.authorizationserver.security.model.Principal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class JwtGeneratorCustomizer implements OAuth2TokenCustomizer<JwtEncodingContext> {

    @Value("${token.exp}")
    private Long exp;

    @Override
    public void customize(JwtEncodingContext context) {

        JwtClaimsSet.Builder claims = context.getClaims();
        RegisteredClient registeredClient = context.getRegisteredClient();
        String clientId = registeredClient.getClientId();
        String issuer = null;
        if (context.getProviderContext() != null) {
            issuer = context.getProviderContext().getIssuer();
        }

        Principal principal = (Principal)context.getPrincipal().getPrincipal();
        List<String> authorities = principal.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());

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
                    claim.put("authorities", (authorities));
                })
                .build();

    }
}
