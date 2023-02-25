package io.oauth.resourceserverrolesresources.user;

import io.oauth.resourceserverrolesresources.role.Role;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Data
public class User {
        private String userId;
        private String providerId;
        private String fullname;
        private String nickname;
        private String phone;
        private String email;
        private LocalDate birth;
        private LocalDateTime regDate;
        private LocalDateTime modDate;
        private Set<Role> roles;
}
