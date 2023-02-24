package io.oauth.oauth2authorizationclientserver.security.filter;

import com.nimbusds.jose.JOSEException;
import io.oauth.oauth2authorizationclientserver.security.common.JwtGenerator;
import io.oauth.oauth2authorizationclientserver.security.model.PrincipalDetails;
import io.oauth.oauth2authorizationclientserver.repository.user.UserRepository;
import io.oauth.oauth2authorizationclientserver.web.domain.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.crypto.keygen.StringKeyGenerator;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.http.converter.OAuth2AccessTokenResponseHttpMessageConverter;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Map;

@Component
public class OAuth2RefreshTokenProcessingFilter extends OncePerRequestFilter {

    private static final RequestMatcher REQUEST_MATCHER = new AntPathRequestMatcher("/oauth2/token", HttpMethod.POST.name());

    @Value("${token.refresh.exp}")
    private Long refreshExpTerm;
    private final UserRepository userRepository;
    private final JwtGenerator jwtGenerator;
    private final StringKeyGenerator refreshTokenGenerator;

    private final HttpMessageConverter<OAuth2AccessTokenResponse> accessTokenHttpResponseConverter =
            new OAuth2AccessTokenResponseHttpMessageConverter();

    public OAuth2RefreshTokenProcessingFilter(UserRepository userRepository, JwtGenerator jwtGenerator, StringKeyGenerator refreshTokenGenerator) {
        this.userRepository = userRepository;
        this.jwtGenerator = jwtGenerator;
        this.refreshTokenGenerator = refreshTokenGenerator;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if(!REQUEST_MATCHER.matches(request) || !request.getParameter("grant_type").equals("refresh_token")){
            filterChain.doFilter(request, response);
            return;
        }else{

            String principalId = request.getParameter("principal_id");
            String refreshTokenValue = request.getParameter("refresh_token");
            String grantType = request.getParameter("grant_type");

            User user = userRepository.findByUserId(principalId);

            if(user == null || !user.getRefreshTokenValue().equals(refreshTokenValue)
                            || user.getRefreshTokenIssuedAt().plus(refreshExpTerm, ChronoUnit.MINUTES).isBefore(LocalDateTime.now())){
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            OAuth2AccessToken accessToken;
            OidcIdToken idToken;
            OAuth2RefreshToken refreshToken;

            PrincipalDetails principalDetails = new PrincipalDetails(user, Collections.emptyMap(), user.getProviderId());

            Instant iat = Instant.now();
            Instant exp = iat.plus(refreshExpTerm, ChronoUnit.MINUTES);

            try {
                idToken = (OidcIdToken) jwtGenerator.generateJwt(principalDetails, OidcParameterNames.ID_TOKEN);
                accessToken = (OAuth2AccessToken) jwtGenerator.generateJwt(principalDetails, OAuth2ParameterNames.ACCESS_TOKEN);
                refreshToken = new OAuth2RefreshToken(refreshTokenGenerator.generateKey(), iat, exp);

            } catch (JOSEException e) {
                throw new RuntimeException(e);
            }

            OAuth2AccessTokenResponse tokenResponse = OAuth2AccessTokenResponse.withToken(accessToken.getTokenValue())
                    .tokenType(OAuth2AccessToken.TokenType.BEARER)
                    .expiresIn(ChronoUnit.SECONDS.between(accessToken.getIssuedAt(), accessToken.getExpiresAt()))
                    .refreshToken(refreshToken.getTokenValue())
                    .additionalParameters(Map.of("id_token", idToken.getTokenValue()))
                    .build();

            this.accessTokenHttpResponseConverter.write(tokenResponse, MediaType.APPLICATION_JSON, new ServletServerHttpResponse(response));
        }

    }
}
