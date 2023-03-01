package io.oauth2.client.role;

import io.oauth2.client.BaseTest;
import io.oauth2.client.resource.Resource;
import io.oauth2.client.role.dto.RoleRequestDto;
import io.oauth2.client.security.WithMockCustomUser;
import org.junit.jupiter.api.BeforeEach;
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

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@Transactional
@SpringBootTest
public class RoleTest extends BaseTest {

    private static final RoleRequestDto parent
            = RoleRequestDto.builder()
                .id("U_1111")
                .description("PARENT")
                .name("ROLE_TEST_PARENT")
                .build();
    private static final RoleRequestDto child
            = RoleRequestDto.builder()
                .id("U_2222")
                .description("CHILD")
                .parentId("U_1111")
                .name("ROLE_TEST_CHILD")
                .build();

    private static final String BASE_URL = "/api/roles";


    @BeforeEach
    @WithMockCustomUser(role = "ROLE_ADMIN")
    void insertBeforeTest() throws Exception {
        mvc.perform(put(BASE_URL+ "/" + parent.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(parent)));

        mvc.perform(put(BASE_URL+ "/" + child.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(child)));
    }

    @DisplayName("Role 등록 테스트 - 인가 실패")
    @WithMockCustomUser(role = "ROLE_MANAGER")
    @Test
    void insertFailureTest() throws Exception {
        RoleRequestDto failure = RoleRequestDto.builder()
                .id("U_3333")
                .description("FAILURE_TEST")
                .name("ROLE_FAILURE_TEST")
                .build();

        mvc.perform(put(BASE_URL+"/"+failure.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(failure)))
                .andExpect(status().isForbidden())
                .andDo(print());
    }


    @DisplayName("Role 수정 테스트")
    @WithMockCustomUser(role = "ROLE_ADMIN")
    @Test
    void updateTest() throws Exception {
        //given
        RoleRequestDto modifiedChild = RoleRequestDto.builder()
                .id(child.getId())
                .name("ROLE_TEST_CHILD_MODIFIED")
                .description("CHILD MODIFIED")
                .build();


        mvc.perform(put(BASE_URL + "/" + modifiedChild.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(modifiedChild)))

                .andExpect(status().isNoContent());
    }

    @DisplayName("Role 한 건 조회 테스트")
    @WithMockCustomUser(role = "ROLE_ADMIN")
    @Test
    void selectOneTest() throws Exception {
        //when
        MvcResult mvcResult = mvc.perform(get(BASE_URL + "/" + child.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andReturn();

        Role result = om.readValue(mvcResult.getResponse().getContentAsString(), Role.class);
        Role parentResult = result.getParent();

        assertThat(result.getId()).isEqualTo(child.getId());
        assertThat(result.getName()).isEqualTo(child.getName());
        assertThat(result.getDescription()).isEqualTo(child.getDescription());
        assertThat(parentResult.getId()).isEqualTo(parent.getId());
        assertThat(parentResult.getName()).isEqualTo(parent.getName());
        assertThat(parentResult.getDescription()).isEqualTo(parent.getDescription());
    }

    @DisplayName("Role 리스트 조회 테스트")
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

        Map result = om.readValue(findWithOffsetSize.getResponse().getContentAsString(), Map.class);

        List<Resource> resources = (List<Resource>) result.get("roles");
        Integer totalCount = (Integer) result.get("totalCount");
        assertThat(totalCount).isGreaterThanOrEqualTo(resources.size());

        MvcResult findAll = mvc.perform(get(BASE_URL + "/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andReturn();

        result = om.readValue(findAll.getResponse().getContentAsString(), Map.class);
        resources = (List<Resource>) result.get("roles");

        assertThat(resources.size()).isEqualTo(totalCount);
    }

    private static Stream offsetAndSizeValues(){
        return Stream.of(
                Arguments.of("0", "5")
        );
    }

    @DisplayName("Role 삭제 테스트")
    @WithMockCustomUser(role = "ROLE_ADMIN")
    @Test
    void deleteTest() throws Exception{
        //when
        mvc.perform(delete(BASE_URL+"/"+parent.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }



}
