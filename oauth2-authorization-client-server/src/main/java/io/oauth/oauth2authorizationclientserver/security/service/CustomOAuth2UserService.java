package io.oauth.oauth2authorizationclientserver.security.service;

import com.nimbusds.jose.JOSEException;
import io.oauth.oauth2authorizationclientserver.security.common.JwtGenerator;
import io.oauth.oauth2authorizationclientserver.security.model.PrincipalDetails;
import io.oauth.oauth2authorizationclientserver.repository.user.UserRepository;
import io.oauth.oauth2authorizationclientserver.utils.UserResolover;
import io.oauth.oauth2authorizationclientserver.web.domain.Role;
import io.oauth.oauth2authorizationclientserver.web.domain.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.keygen.StringKeyGenerator;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequestEntityConverter;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.UnknownContentTypeException;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Component
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    @Value("${token.refresh.exp}")
    private Long refreshExpTerm;

    private static final String INVALID_USER_INFO_RESPONSE_ERROR_CODE = "invalid_user_info_response";

    private static final ParameterizedTypeReference<Map<String, Object>> PARAMETERIZED_RESPONSE_TYPE = new ParameterizedTypeReference<Map<String, Object>>() {
    };

    private final UserResolover userResolover;

    private Converter<OAuth2UserRequest, RequestEntity<?>> requestEntityConverter = new OAuth2UserRequestEntityConverter();

    private final UserRepository userRepository;

    private final StringKeyGenerator refreshTokenGenerator;

    private final JwtGenerator jwtGenerator;

    private RestOperations restOperations;

    public CustomOAuth2UserService(UserRepository userRepository, StringKeyGenerator refreshTokenGenerator, JwtGenerator jwtGenerator) {
        this.userRepository = userRepository;
        this.refreshTokenGenerator = refreshTokenGenerator;
        this.jwtGenerator = jwtGenerator;
        this.restOperations = new RestTemplate();
        this.userResolover = new UserResolover();
    }

    @Transactional
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        //Authorization Server(Social)????????? userinfo ????????????.
        RequestEntity<?> request = this.requestEntityConverter.convert(userRequest);
        ResponseEntity<Map<String, Object>> response = getResponse(userRequest, request);
        Map<String, Object> userAttributes = response.getBody();


        //OAuth2 Provider??? ?????? ????????? ??????, key?????? ???????????? ?????? ?????? ?????? ?????????.
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        User user = userResolover.resolve(registrationId, userAttributes);


        //DB ???????????? ??????
        User foundUser = userRepository
                .findByUserId(user.getUserId());

        Set<Role> roles = foundUser == null ? Set.of(Role.getDefaultUserRole()) : foundUser.getRoles();
        user.setRoles(roles);

        //Authentication Token??? ?????? principal
        PrincipalDetails principalDetails = null;
        //Token ??????(Id, Access, Refresh)
        principalDetails = setTokenToUser(userAttributes, registrationId, user);

        if(foundUser == null){
            userRepository.insert(user);
        } else {
            userRepository.update(user);
        }

        /**
         * ??? ??????????????? ???????????? ????????? Client?????? Token??? ?????????.
         *  {@link io.oauth.oauth2authorizationclientserver.security.handler.SuccessfulAuthenticationJwtResponseHandler}
         */
        return principalDetails;
    }

    //principalDetails ????????? ???????????? Jwt(Access, ID)?????? ??? SocialUser??? Set.
    private PrincipalDetails setTokenToUser(Map<String, Object> userAttributes, String registrationId, User user) {
        PrincipalDetails principalDetails;
        principalDetails = new PrincipalDetails(user, userAttributes, registrationId);

        OAuth2AccessToken accessToken;
        OidcIdToken idToken;
        OAuth2RefreshToken refreshToken;

        Instant iat = LocalDateTime.now().toInstant(ZoneOffset.UTC);
        Instant exp = iat.plus(refreshExpTerm, ChronoUnit.MINUTES);

        try {
            accessToken = (OAuth2AccessToken) jwtGenerator.generateJwt(principalDetails, OAuth2ParameterNames.ACCESS_TOKEN);
            idToken = (OidcIdToken) jwtGenerator.generateJwt(principalDetails, OidcParameterNames.ID_TOKEN);
            refreshToken = new OAuth2RefreshToken(refreshTokenGenerator.generateKey(), iat, exp);

            //principal??? add Token
            principalDetails.setAccessToken(accessToken);
            principalDetails.setIdToken(idToken);
            principalDetails.setRefreshToken(refreshToken);

            //add RefreshToken, issuedAt to user(?????? ?????? ????????? ?????????.)
            user.setRefreshTokenValue(refreshToken.getTokenValue());
            user.setRefreshTokenIssuedAt(LocalDateTime.ofInstant(iat, Clock.systemUTC().getZone()));

        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
        return principalDetails;
    }

    private ResponseEntity<Map<String, Object>> getResponse(OAuth2UserRequest userRequest, RequestEntity<?> request) {
        try {
            return this.restOperations.exchange(request, PARAMETERIZED_RESPONSE_TYPE);
        }
        catch (OAuth2AuthorizationException ex) {
            OAuth2Error oauth2Error = ex.getError();
            StringBuilder errorDetails = new StringBuilder();
            errorDetails.append("Error details: [");
            errorDetails.append("UserInfo Uri: ")
                    .append(userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUri());
            errorDetails.append(", Error Code: ").append(oauth2Error.getErrorCode());
            if (oauth2Error.getDescription() != null) {
                errorDetails.append(", Error Description: ").append(oauth2Error.getDescription());
            }
            errorDetails.append("]");
            oauth2Error = new OAuth2Error(INVALID_USER_INFO_RESPONSE_ERROR_CODE,
                    "An error occurred while attempting to retrieve the UserInfo Resource: " + errorDetails.toString(),
                    null);
            throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString(), ex);
        }
        catch (UnknownContentTypeException ex) {
            String errorMessage = "An error occurred while attempting to retrieve the UserInfo Resource from '"
                    + userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUri()
                    + "': response contains invalid content type '" + ex.getContentType().toString() + "'. "
                    + "The UserInfo Response should return a JSON object (content type 'application/json') "
                    + "that contains a collection of name and value pairs of the claims about the authenticated End-User. "
                    + "Please ensure the UserInfo Uri in UserInfoEndpoint for Client Registration '"
                    + userRequest.getClientRegistration().getRegistrationId() + "' conforms to the UserInfo Endpoint, "
                    + "as defined in OpenID Connect 1.0: 'https://openid.net/specs/openid-connect-core-1_0.html#UserInfo'";
            OAuth2Error oauth2Error = new OAuth2Error(INVALID_USER_INFO_RESPONSE_ERROR_CODE, errorMessage, null);
            throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString(), ex);
        }
        catch (RestClientException ex) {
            OAuth2Error oauth2Error = new OAuth2Error(INVALID_USER_INFO_RESPONSE_ERROR_CODE,
                    "An error occurred while attempting to retrieve the UserInfo Resource: " + ex.getMessage(), null);
            throw new OAuth2AuthenticationException(oauth2Error, oauth2Error.toString(), ex);
        }
    }
}
