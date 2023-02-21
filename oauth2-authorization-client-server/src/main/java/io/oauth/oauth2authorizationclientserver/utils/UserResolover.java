package io.oauth.oauth2authorizationclientserver.utils;

import io.oauth.oauth2authorizationclientserver.web.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@Component
public class UserResolover {

    private final PasswordEncoder passwordEncoder;

    public User resolve(String registrationId, Map<String, Object> attributes){
        User user = null;
        if(registrationId.equals("google")){
            user = ofGoogle(registrationId, attributes);
        } else if(registrationId.equals("naver")){
            attributes = (Map<String, Object>)attributes.get("response");
            user = ofNaver(registrationId, attributes);
        }

        return user;
    }

    private User ofGoogle(String registrationId, Map<String, Object> attributes) {
        User user = new User((String) attributes.get("sub"), registrationId);
        user.setPassword("{noop}"+UUID.randomUUID());
        user.setEmail((String)attributes.get("email"));
        user.setFullname((String)attributes.get("name"));
        user.setNickname(attributes.get("name") +"_"+ UUID.randomUUID());
        user.setRoles(new HashSet<>());

        return user;
    }

    private User ofNaver(String registrationId, Map<String, Object> attributes) {
        String userId = (String)attributes.get("id");
        String nickname = (String)attributes.get("nickname");
        String email    = (String)attributes.get("email");
        String phone    = (String)attributes.get("mobile");
        String fullName = (String)attributes.get("name");
        String birthYear= (String)attributes.get("birthyear");
        String[] birthDay = ((String)attributes.get("birthday")).split("-");

        User user = new User(userId, registrationId);
        user.setNickname(nickname);
        user.setPassword("{noop}"+UUID.randomUUID());
        user.setEmail(email);
        user.setPhone(phone);
        user.setFullname(fullName);
        user.setRoles(new HashSet<>());
        user.setBirth(LocalDate.of(Integer.parseInt(birthYear)
                        , Integer.parseInt(birthDay[0])
                        , Integer.parseInt(birthDay[1])));

        return user;
    }

}
