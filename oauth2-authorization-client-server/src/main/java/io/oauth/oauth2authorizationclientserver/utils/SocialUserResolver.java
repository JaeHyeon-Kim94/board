package io.oauth.oauth2authorizationclientserver.utils;

import io.oauth.oauth2authorizationclientserver.web.domain.SocialUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@Component
public class SocialUserResolver {

    private final PasswordEncoder passwordEncoder;

    public SocialUser resolve(String registrationId, Map<String, Object> attributes){
        SocialUser socialUser = null;
        if(registrationId.equals("google")){
            socialUser = ofGoogle(registrationId, attributes);
        } else if(registrationId.equals("naver")){
            attributes = (Map<String, Object>)attributes.get("response");
            socialUser = ofNaver(registrationId, attributes);
        }

        return socialUser;
    }

    private SocialUser ofGoogle(String registrationId, Map<String, Object> attributes) {
        return SocialUser.builder()
                .clientRegistrationId(registrationId)
                .socialUserId((String)attributes.get("sub"))
                .email((String)attributes.get("email"))
                .fullName((String)attributes.get("name"))
                .nickname(attributes.get("name") +"_"+ UUID.randomUUID())
                .roles(new HashSet<>())
                .build();
    }

    private SocialUser ofNaver(String registrationId, Map<String, Object> attributes) {
        String username = (String)attributes.get("id");
        String nickname = (String)attributes.get("nickname");
        String email    = (String)attributes.get("email");
        String phone    = (String)attributes.get("mobile");
        String fullName = (String)attributes.get("name");
        String birthYear= (String)attributes.get("birthyear");
        String[] birthDay = ((String)attributes.get("birthday")).split("-");

        return SocialUser.builder()
                .clientRegistrationId(registrationId)
                .socialUserId(username)
                .nickname(nickname)
                .email(email)
                .phone(phone)
                .fullName(fullName)
                .roles(new HashSet<>())
                .birth(LocalDate.of(Integer.parseInt(birthYear)
                        , Integer.parseInt(birthDay[0])
                        , Integer.parseInt(birthDay[1])))
                .build();
    }

}
