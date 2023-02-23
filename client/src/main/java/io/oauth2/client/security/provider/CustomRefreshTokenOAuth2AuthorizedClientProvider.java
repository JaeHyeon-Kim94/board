package io.oauth2.client.security.provider;

import io.oauth2.client.security.converter.CustomOAuth2RefreshTokenGrantRequestEntityConverter;
import io.oauth2.client.security.model.CustomOAuth2AuthorizedClient;
import io.oauth2.client.security.utils.JwtUtils;
import org.springframework.lang.Nullable;
import org.springframework.security.oauth2.client.ClientAuthorizationException;
import org.springframework.security.oauth2.client.OAuth2AuthorizationContext;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.endpoint.DefaultRefreshTokenTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2RefreshTokenGrantRequest;
import org.springframework.security.oauth2.core.OAuth2AuthorizationException;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.util.Assert;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class CustomRefreshTokenOAuth2AuthorizedClientProvider implements OAuth2AuthorizedClientProvider {

    private OAuth2AccessTokenResponseClient<OAuth2RefreshTokenGrantRequest> accessTokenResponseClient = new DefaultRefreshTokenTokenResponseClient();

    private CustomOAuth2RefreshTokenGrantRequestEntityConverter converter = new CustomOAuth2RefreshTokenGrantRequestEntityConverter();

    @Override
    @Nullable
    public OAuth2AuthorizedClient authorize(OAuth2AuthorizationContext context) {
        Assert.notNull(context, "context cannot be null");
        OAuth2AuthorizedClient authorizedClient = context.getAuthorizedClient();
        if (authorizedClient == null || authorizedClient.getRefreshToken() == null
                || !hasTokenExpired(authorizedClient.getAccessToken())) {
            return null;
        }
        Object requestScope = context.getAttribute(OAuth2AuthorizationContext.REQUEST_SCOPE_ATTRIBUTE_NAME);
        Set<String> scopes = Collections.emptySet();
        if (requestScope != null) {
            Assert.isInstanceOf(String[].class, requestScope, "The context attribute must be of type String[] '"
                    + OAuth2AuthorizationContext.REQUEST_SCOPE_ATTRIBUTE_NAME + "'");
            scopes = new HashSet<>(Arrays.asList((String[]) requestScope));
        }
        OAuth2RefreshTokenGrantRequest refreshTokenGrantRequest = new OAuth2RefreshTokenGrantRequest(
                authorizedClient.getClientRegistration(), authorizedClient.getAccessToken(),
                authorizedClient.getRefreshToken(), scopes);

        ((DefaultRefreshTokenTokenResponseClient)accessTokenResponseClient).setRequestEntityConverter(converter);

        OAuth2AccessTokenResponse tokenResponse = getTokenResponse(authorizedClient, refreshTokenGrantRequest);

        String idToken = (String) tokenResponse.getAdditionalParameters().get("id_token");
        OidcIdToken oidcIdToken = JwtUtils.convertTokenValueStringToOAuth2Token(idToken, OidcParameterNames.ID_TOKEN);

        return new CustomOAuth2AuthorizedClient(context.getAuthorizedClient().getClientRegistration(),
                context.getPrincipal().getName(), tokenResponse.getAccessToken(), tokenResponse.getRefreshToken(), oidcIdToken);
    }

    private OAuth2AccessTokenResponse getTokenResponse(OAuth2AuthorizedClient authorizedClient,
                                                       OAuth2RefreshTokenGrantRequest refreshTokenGrantRequest) {
        try {
            return this.accessTokenResponseClient.getTokenResponse(refreshTokenGrantRequest);
        } catch (OAuth2AuthorizationException ex) {
            throw new ClientAuthorizationException(ex.getError(),
                    authorizedClient.getClientRegistration().getRegistrationId(), ex);
        }
    }

    private boolean hasTokenExpired(OAuth2Token token) {
        return token.getExpiresAt().isBefore(Instant.now());
    }

    public void setAccessTokenResponseClient(
            OAuth2AccessTokenResponseClient<OAuth2RefreshTokenGrantRequest> accessTokenResponseClient) {
        Assert.notNull(accessTokenResponseClient, "accessTokenResponseClient cannot be null");
        this.accessTokenResponseClient = accessTokenResponseClient;
    }
}