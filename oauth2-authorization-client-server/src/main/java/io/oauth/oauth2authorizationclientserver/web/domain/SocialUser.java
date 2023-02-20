package io.oauth.oauth2authorizationclientserver.web.domain;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@EqualsAndHashCode(exclude = {"password", "modDate"
        , "regDate", "roles", "idToken"
        , "refreshToken", "accessToken"})
@Builder
public class SocialUser {

    private String socialUserId;
    private String clientRegistrationId;
    private String fullName;
    private String nickname;

    private String phone;
    private String email;

    private LocalDate birth;
    private LocalDateTime regDate;
    private LocalDateTime modDate;
    private Set<Role> roles;


}
