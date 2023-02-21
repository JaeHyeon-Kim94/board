package io.oauth.oauth2authorizationclientserver.web.domain;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@EqualsAndHashCode(exclude =
        {"password", "modDate", "providerId"
        , "regDate", "roles"
        })
public class User {

    private String userId;
    private String providerId;
    private String password;
    private String fullname;
    private String nickname;
    private String phone;
    private String email;
    private LocalDate birth;
    private String refreshTokenValue;
    private LocalDateTime refreshTokenIssuedAt;
    private LocalDateTime regDate;
    private LocalDateTime modDate;
    private Set<Role> roles;

    public User(String userId, String providerId) {
        this.userId = providerId+"_"+userId;
        this.providerId = providerId;
    }
}
