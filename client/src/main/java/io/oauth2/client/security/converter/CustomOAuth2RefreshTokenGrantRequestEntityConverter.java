
package io.oauth2.client.security.converter;

import io.oauth2.client.security.utils.JwtUtils;
import org.springframework.security.oauth2.client.endpoint.OAuth2RefreshTokenGrantRequest;
import org.springframework.security.oauth2.client.endpoint.OAuth2RefreshTokenGrantRequestEntityConverter;
import org.springframework.util.MultiValueMap;

import java.util.Map;

public class CustomOAuth2RefreshTokenGrantRequestEntityConverter
        extends OAuth2RefreshTokenGrantRequestEntityConverter {

    @Override
    protected MultiValueMap<String, String> createParameters(OAuth2RefreshTokenGrantRequest refreshTokenGrantRequest) {
        MultiValueMap<String, String> parameters = super.createParameters(refreshTokenGrantRequest);

        if(refreshTokenGrantRequest.getClientRegistration().getRegistrationId().equals("myOAuth")){
            return parameters;
        }

        String tokenValue = refreshTokenGrantRequest.getAccessToken().getTokenValue();
        Map<String, Object> claims = JwtUtils.getClaims(tokenValue);
        
        parameters.add("principal_id", (String) claims.get("sub"));
        return parameters;
    }
}
