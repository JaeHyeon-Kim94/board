package io.oauth.authorizationserver.web.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.HashSet;

@Getter
@Setter
@ToString
public class UserJoinDto {

    @NotBlank
    @Pattern(regexp = "^(\\w)*$")
    @Length(min=3, max=10)
    private String userId;

    @NotBlank
    private String password;

    @NotBlank
    @Pattern(regexp = "^([\\w|가-힣|\\s])*$")
    private String fullname;

    @NotBlank
    @Length(min=3, max=10)
    private String nickname;

    @NotBlank
    @Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$")
    private String phone;

    @NotNull
    @Pattern(regexp = "\\w+@\\w+\\.\\w+(\\.\\w+)?")
    private String email;

    @NotNull
    private LocalDate birth;

    public static User toUser(UserJoinDto dto){
        return User.builder()
                .userId(dto.getUserId())
                .providerId("myOAUth")
                .password(dto.getPassword())
                .fullname(dto.getFullname())
                .nickname(dto.getNickname())
                .phone(dto.getPhone())
                .email(dto.getEmail())
                .birth(dto.getBirth())
                .roles(new HashSet<>())
                .build();
    }

}
