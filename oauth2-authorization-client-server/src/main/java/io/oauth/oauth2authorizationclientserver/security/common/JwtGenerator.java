package io.oauth.oauth2authorizationclientserver.security.common;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.jwk.JWK;
import io.oauth.oauth2authorizationclientserver.security.signer.SecuritySigner;
import io.oauth.oauth2authorizationclientserver.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.time.Instant;
import java.util.Date;
import java.util.Map;

public class JwtGenerator {

    private final SecuritySigner securitySigner;
    private final JWK jwk;

    @Value("${token.audience}")
    String audience;


    public JwtGenerator(SecuritySigner securitySigner, JWK jwk) {
        this.securitySigner = securitySigner;
        this.jwk = jwk;
    }

    public OAuth2Token generateJwt(OAuth2User user, String type) throws JOSEException {

        String jwtToken = securitySigner.getJwtToken(user, jwk, type);
        Map<String, Object> claims = JwtUtils.getClaims(jwtToken);

        Instant iat = Instant.ofEpochSecond(Long.valueOf((Integer) claims.get("iat")));
        Instant exp = Instant.ofEpochSecond(Long.valueOf((Integer) claims.get("exp")));
        if(type.equals(OAuth2ParameterNames.ACCESS_TOKEN)){
            return new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER
                    , jwtToken, iat, exp);
        }else if(type.equals((OidcParameterNames.ID_TOKEN))){
            return new OidcIdToken(jwtToken, iat, exp, claims);
        }

        return null;
    }


}
