package io.oauth.authorizationserver.web.domain;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@ToString
@EqualsAndHashCode(exclude = {"roles", "modDate"})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    private String userId;
    private String providerId;
    private String password;
    private String fullname;
    private String nickname;
    private String phone;
    private String email;
    private LocalDate birth;
    private LocalDateTime regDate;
    private LocalDateTime modDate;
    private Set<Role> roles = new HashSet<>();
}
