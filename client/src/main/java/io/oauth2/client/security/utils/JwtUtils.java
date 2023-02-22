package io.oauth2.client.security.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.Base64;
import java.util.Map;

public class JwtUtils {

    private static final ObjectMapper om = new ObjectMapper();

    /**
     * @param tokenValue Base64Encoded stringValue of jwt.
     * @return Mapped key-value from claims(JSON String)
     */
    public static Map<String, Object> getClaims(String tokenValue) {
        String[] split = tokenValue.split("\\.");
        String claims = new String(Base64.getDecoder().decode(split[1]));

        try {
            return om.readValue(claims, Map.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("An error occurred during processing IdTokenStringValue to OidcIdToken", e);
        }
    }

    public static <T extends OAuth2Token> T convertTokenValueStringToOAuth2Token(String tokenValue, String type){
        if(!StringUtils.hasText(tokenValue) && tokenValue.split("\\.").length != 3){
            return null;
        }

        Map<String, Object> claims = getClaims(tokenValue);

        Instant iat = Instant.ofEpochSecond((Integer) claims.get("iat"));
        Instant exp = Instant.ofEpochSecond((Integer) claims.get("exp"));
        OAuth2Token token = null;

        if(type.equals(OAuth2ParameterNames.ACCESS_TOKEN)){
            token = new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, tokenValue, iat, exp);
        } else if(type.equals(OidcParameterNames.ID_TOKEN)){
            token = new OidcIdToken(tokenValue, iat, exp, claims);
        }


        return (T) token;
    }

}
