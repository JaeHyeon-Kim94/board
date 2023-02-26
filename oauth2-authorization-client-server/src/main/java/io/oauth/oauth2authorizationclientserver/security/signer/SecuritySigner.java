package io.oauth.oauth2authorizationclientserver.security.signer;

import com.nimbusds.jose.*;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import io.oauth.oauth2authorizationclientserver.security.model.PrincipalDetails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public abstract class SecuritySigner {

    @Value("${token.issuer}")
    private String issuer;

    @Value("${token.audience}")
    private String audience;

    @Value("${token.exp}")
    private Long expTerm;


    protected String getJwtTokenInternal(JWSSigner jwsSigner, OAuth2User user, JWK jwk, String tokenType) throws JOSEException {
        JWSHeader header = new JWSHeader.Builder((JWSAlgorithm) jwk.getAlgorithm()).keyID(jwk.getKeyID()).type(JOSEObjectType.JWT).build();

        Instant now = Instant.now();
        Date iat = Date.from(now);
        Date exp = Date.from(now.plus(expTerm, ChronoUnit.MINUTES));


        JWTClaimsSet jwtClaimsSet = buildTokenClaims(user, iat, exp);

        SignedJWT signedJWT = new SignedJWT(header,jwtClaimsSet);
        signedJWT.sign(jwsSigner);
        String jwtToken = signedJWT.serialize();

        return jwtToken;
    }

    private JWTClaimsSet buildTokenClaims(OAuth2User user, Date iat, Date exp) {
        PrincipalDetails principal = (PrincipalDetails) user;
        List<String> authorities = principal.getAuthorities().stream().map(auth -> auth.getAuthority()).collect(Collectors.toList());

        return new JWTClaimsSet.Builder()
                .subject(principal.getName())
                .issuer(issuer)
                .audience(audience)
                .claim("username",principal.getUsername())
                .claim("nickname", principal.getNickname())
                .issueTime(iat)
                .expirationTime(exp)
                .notBeforeTime(iat)
                .claim("authorities", authorities)
                .build();
    }

    public abstract String getJwtToken(OAuth2User user, JWK jwk, String tokenType) throws JOSEException;
}
