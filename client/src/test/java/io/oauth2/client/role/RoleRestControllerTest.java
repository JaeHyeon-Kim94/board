package io.oauth2.client.role;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.oauth2.client.resource.Resource;
import io.oauth2.client.role.dto.RoleRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@Transactional
@AutoConfigureMockMvc(addFilters = false)
public class RoleRestControllerTest {

    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper om;

    RoleRequestDto parent;
    RoleRequestDto child;
    String url = "/api/roles";


    @BeforeEach
    void beforeEach() throws Exception {
        parent = RoleRequestDto.builder()
                .id("U_1111")
                .description("PARENT")
                .name("ROLE_TEST_PARENT")
                .build();

        child = RoleRequestDto.builder()
                .id("U_2222")
                .description("CHILD")
                .parentId("U_1111")
                .name("ROLE_TEST_CHILD")
                .build();


        mvc.perform(put(url+ "/" + parent.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsBytes(parent)))

                .andExpect(status().isCreated());


        mvc.perform(put(url+ "/" + child.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(child)))

                        .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Role 수정 테스트")
    void updateTest() throws Exception {
        //given
        RoleRequestDto modifiedChild = RoleRequestDto.builder()
                .id(child.getId())
                .name("ROLE_TEST_CHILD_MODIFIED")
                .description("CHILD MODIFIED")
                .build();


        mvc.perform(put(url + "/" + modifiedChild.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(modifiedChild)))

                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Role 한 건 조회 테스트")
    void selectOneTest() throws Exception {
        //when
        MvcResult mvcResult = mvc.perform(get(url + "/" + child.getId())
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

    @ParameterizedTest
    @MethodSource("offsetAndSizeValues")
    @DisplayName("Role 리스트 조회 테스트")
    void selectList(long offset, int size) throws Exception {
        //when
        MvcResult findWithOffsetSize = mvc.perform(get(url + "?offset=" + offset + "&size=" + size)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        Map result = om.readValue(findWithOffsetSize.getResponse().getContentAsString(), Map.class);

        List<Resource> resources = (List<Resource>) result.get("roles");
        Integer totalCount = (Integer) result.get("totalCount");
        assertThat(totalCount).isGreaterThanOrEqualTo(resources.size());

        MvcResult findAll = mvc.perform(get(url + "/")
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
                Arguments.of(0L, 5)
        );
    }

    @Test
    @DisplayName("Role 삭제 테스트")
    void deleteTest() throws Exception{
        //when
        mvc.perform(delete(url+"/"+parent.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }



}
