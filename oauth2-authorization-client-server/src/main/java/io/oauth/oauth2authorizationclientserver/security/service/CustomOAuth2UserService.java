package io.oauth.oauth2authorizationclientserver.security.service;

import com.nimbusds.jose.JOSEException;
import io.oauth.oauth2authorizationclientserver.security.common.JwtGenerator;
import io.oauth.oauth2authorizationclientserver.security.model.PrincipalDetails;
import io.oauth.oauth2authorizationclientserver.security.repository.socialuser.SocialUserRepository;
import io.oauth.oauth2authorizationclientserver.utils.SocialUserResolver;
import io.oauth.oauth2authorizationclientserver.web.domain.Role;
import io.oauth.oauth2authorizationclientserver.web.domain.SocialUser;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
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

import java.util.*;

@Component
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private static final String MISSING_USER_INFO_URI_ERROR_CODE = "missing_user_info_uri";

    private static final String MISSING_USER_NAME_ATTRIBUTE_ERROR_CODE = "missing_user_name_attribute";

    private static final String INVALID_USER_INFO_RESPONSE_ERROR_CODE = "invalid_user_info_response";

    private static final ParameterizedTypeReference<Map<String, Object>> PARAMETERIZED_RESPONSE_TYPE = new ParameterizedTypeReference<Map<String, Object>>() {
    };

    private final SocialUserResolver socialUserResolver;

    private Converter<OAuth2UserRequest, RequestEntity<?>> requestEntityConverter = new OAuth2UserRequestEntityConverter();

    private final SocialUserRepository socialUserRepository;

    private final JwtGenerator jwtGenerator;

    private RestOperations restOperations;

    public CustomOAuth2UserService(SocialUserRepository socialUserRepository, PasswordEncoder passwordEncoder, JwtGenerator jwtGenerator) {
        this.socialUserRepository = socialUserRepository;
        this.jwtGenerator = jwtGenerator;
        this.restOperations = new RestTemplate();
        this.socialUserResolver = new SocialUserResolver(passwordEncoder);
    }

    @Transactional
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        //Authorization Server(Social)로부터 userinfo 받아온다.
        RequestEntity<?> request = this.requestEntityConverter.convert(userRequest);
        ResponseEntity<Map<String, Object>> response = getResponse(userRequest, request);
        Map<String, Object> userAttributes = response.getBody();


        //OAuth2 Provider에 따라 구조, key등이 다르므로 이에 대한 처리.
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        SocialUser socialUser = socialUserResolver.resolve(registrationId, userAttributes);


        //Authentication Token에 담길 principal
        PrincipalDetails principalDetails = null;
        //DB 트랜잭션 시작
        SocialUser foundSocialUser = socialUserRepository
                .findByUsernameAndClientRegistrationId(socialUser.getSocialUserId(), registrationId);

        //1. inser of update
        //2. Access, ID 토큰 발급, 저장
        if(foundSocialUser == null){
            socialUser.setRoles(Set.of(new Role("ROLE_USER")));
            socialUserRepository.insert(socialUser);
        } else {
            socialUser.setRoles(foundSocialUser.getRoles());
            socialUserRepository.update(socialUser);
        }
        principalDetails = setTokenToSocialUser(userAttributes, registrationId, socialUser);

        return principalDetails;
    }

    //principalDetails 데이터 바탕으로 Jwt(Access, ID)발급 후 SocialUser에 Set.
    private PrincipalDetails setTokenToSocialUser(Map<String, Object> userAttributes, String registrationId, SocialUser socialUser) {
        PrincipalDetails principalDetails;
        principalDetails = new PrincipalDetails(socialUser, userAttributes, registrationId);
        OAuth2AccessToken accessToken;
        OidcIdToken idToken;
        try {
            accessToken = (OAuth2AccessToken) jwtGenerator.generateJwt(principalDetails, OAuth2ParameterNames.ACCESS_TOKEN);
            idToken = (OidcIdToken) jwtGenerator.generateJwt(principalDetails, OidcParameterNames.ID_TOKEN);

            principalDetails.setAccessToken(accessToken);
            principalDetails.setIdToken(idToken);
            principalDetails.setRefreshToken(null);
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
