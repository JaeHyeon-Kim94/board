package io.oauth2.client.user;

import io.oauth2.client.BaseTest;
import io.oauth2.client.role.Role;
import io.oauth2.client.security.WithMockCustomUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@AutoConfigureMockMvc
@Transactional
@SpringBootTest
public class UserRestControllerTest extends BaseTest {

    private static final String BASE_URL = "/api/users";

    private static final String USER_ID = "user";
    private static final String ROLE_ID = "U_0000";

    @DisplayName("사용자 권한 등록 테스트 - 인가 성공")
    @WithMockCustomUser(role = "ROLE_ADMIN")
    @Test
    void insertSuccessTest() throws Exception {
        mvc.perform(post(BASE_URL+"/"+USER_ID+"/roles/"+ROLE_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @DisplayName("사용자 권한 등록 테스트 - 인가 실패")
    @WithMockCustomUser(role = "ROLE_MANAGER")
    @Test
    void insertFailureTest() throws Exception {
        mvc.perform(post(BASE_URL+"/"+USER_ID+"/roles/"+ROLE_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @DisplayName("사용자 권한 삭제 테스트")
    @WithMockCustomUser(role = "ROLE_ADMIN")
    @Test
    void deleteSuccessTest() throws Exception {
        mvc.perform(delete(BASE_URL+"/"+USER_ID+"/roles/"+ROLE_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @DisplayName("사용자 조회")
    @WithMockCustomUser(role = "ROLE_ADMIN")
    @Test
    void selectOneTest() throws Exception {
        MvcResult mvcResult = mvc.perform(get(BASE_URL + "/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        User user = mapJsonToObject(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8), User.class);
        Set<Role> roles = user.getRoles();
        Role role = (Role)roles.toArray()[0];
        assertThat(user.getUserId()).isEqualTo("user");
        assertThat(user.getProviderId()).isEqualTo("myOAuth");
        assertThat(user.getFullname()).isEqualTo("USER 성명");
        assertThat(user.getNickname()).isEqualTo("USER 닉네임");
        assertThat(user.getPhone()).isEqualTo("010-0000-0000");
        assertThat(user.getEmail()).isEqualTo("user@email.com");
        assertThat(user.getBirth()).isEqualTo(LocalDate.of(1999, 9, 9));
        assertThat(roles.size()).isEqualTo(1);
        assertThat(role.getId()).isEqualTo("U_0000");
        assertThat(role.getName()).isEqualTo("ROLE_USER");
    }


    @DisplayName("User 리스트 조회 테스트")
    @WithMockCustomUser(role = "ROLE_ADMIN")
    @MethodSource("offsetAndSizeValues")
    @ParameterizedTest
    void selectList(String offset, String size) throws Exception {
        //when
        MvcResult findWithOffsetSize = mvc.perform(get(BASE_URL)
                        .param("offset", offset)
                        .param("size", size)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        Map result = mapJsonToObject(findWithOffsetSize.getResponse().getContentAsString(), Map.class);

        List<User> users = (List<User>) result.get("users");
        Integer totalCount = (Integer) result.get("totalCount");
        assertThat(totalCount).isGreaterThanOrEqualTo(users.size());

        MvcResult findAll = mvc.perform(get(BASE_URL + "/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andReturn();

        result = mapJsonToObject(findAll.getResponse().getContentAsString(StandardCharsets.UTF_8), Map.class);
        users = (List<User>) result.get("users");

        assertThat(users.size()).isEqualTo(totalCount);
    }

    private static Stream offsetAndSizeValues(){
        return Stream.of(
                Arguments.of("0", "5")
        );
    }



}
