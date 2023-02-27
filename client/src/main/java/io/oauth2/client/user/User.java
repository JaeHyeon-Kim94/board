package io.oauth2.client.user;

import io.oauth2.client.role.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Builder
@NoArgsConstructor
@AllArgsConstructor
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
