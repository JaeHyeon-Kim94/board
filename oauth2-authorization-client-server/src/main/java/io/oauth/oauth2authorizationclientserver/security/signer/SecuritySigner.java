package io.oauth.oauth2authorizationclientserver.security.signer;

import com.nimbusds.jose.*;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import io.oauth.oauth2authorizationclientserver.security.model.PrincipalDetails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
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


        JWTClaimsSet jwtClaimsSet = tokenType.equals(OAuth2ParameterNames.ACCESS_TOKEN) ?
                buildAccessTokenClaims(user, iat, exp) : buildIdTokenClaims(user, iat, exp);

        SignedJWT signedJWT = new SignedJWT(header,jwtClaimsSet);
        signedJWT.sign(jwsSigner);
        String jwtToken = signedJWT.serialize();

        return jwtToken;
    }

    private JWTClaimsSet buildIdTokenClaims(OAuth2User user, Date iat, Date exp) {
        PrincipalDetails principal = (PrincipalDetails) user;

        return new JWTClaimsSet.Builder()
                .subject(principal.getUsername())
                .issuer(issuer)
                .audience(audience)
                .claim("fullname",principal.getName())
                .claim("nickname", principal.getNickname())
                .issueTime(iat)
                .expirationTime(exp)
                .notBeforeTime(iat)
                .build();

    }

    private JWTClaimsSet buildAccessTokenClaims(OAuth2User user, Date iat, Date exp) {

        List<String> authorities = user.getAuthorities().stream().map(auth -> auth.getAuthority()).collect(Collectors.toList());

        return new JWTClaimsSet.Builder()
                .subject(user.getName())
                .issuer(issuer)
                .audience(audience)
                .claim("username",user.getAttribute("name"))
                .claim("nickname", user.getAttribute("nickname"))
                .issueTime(iat)
                .expirationTime(exp)
                .notBeforeTime(iat)
                .claim("authorities", authorities)
                .build();
    }

    public abstract String getJwtToken(OAuth2User user, JWK jwk, String tokenType) throws JOSEException;
}
