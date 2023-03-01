package io.oauth2.client.resource;

import io.oauth2.client.BaseTest;
import io.oauth2.client.resource.dto.ResourceRequestDto;
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

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@Transactional
@SpringBootTest
class ResourceTest extends BaseTest {
    private static final ResourceRequestDto requestDto
            = ResourceRequestDto.builder()
                .type("BASE_URL")
                .level(1111L)
                .roleId("U_0000")
                .value("/api/test")
                .httpMethod(null)
                .build();
    private static final String BASE_URL = "/api/resources";
    Long id;

    @BeforeEach
    @WithMockCustomUser(role = "ROLE_ADMIN")
    void before() throws Exception {

        String resource = om.writeValueAsString(requestDto);

        mvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(resource))
                .andExpect(result ->
                        id = Long.valueOf(result.getResponse()
                                            .getHeader("Location")
                                            .replace("/api/resources/", "")));
    }


    @DisplayName("Resource 수정 테스트")
    @WithMockCustomUser(role = "ROLE_ADMIN")
    @Test
    void updateTest() throws Exception {
        //given
        ResourceRequestDto dto = ResourceRequestDto.builder()
                .type("url modified")
                .level(999L)
                .roleId("M_0000")
                .value("/api/test-modified")
                .build();

        //when
        mvc.perform(put(BASE_URL +"/"+id)
                .content(om.writeValueAsBytes(dto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @DisplayName("Resource 한 건 조회 테스트")
    @WithMockCustomUser(role = "ROLE_ADMIN")
    @Test
    void selectOneTest() throws Exception{
        //when
        MvcResult mvcResult = mvc.perform(get(BASE_URL + "/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        Resource resource = om.readValue(mvcResult.getResponse().getContentAsString(), Resource.class);

        assertThat(id).isEqualTo(resource.getId());
        assertThat(requestDto.getLevel()).isEqualTo(resource.getLevel());
        assertThat(requestDto.getType()).isEqualTo(resource.getType());
        assertThat(requestDto.getRoleId()).isEqualTo(resource.getRole().getId());
        assertThat(requestDto.getValue()).isEqualTo(resource.getValue());
    }


    @DisplayName("Resource 리스트 조회 테스트")
    @MethodSource("offsetAndSizeValues")
    @WithMockCustomUser(role = "ROLE_ADMIN")
    @ParameterizedTest
    void selectList(String offset,String size) throws Exception{
        //when
        MvcResult findWithOffsetSize = mvc.perform(get(BASE_URL)
                        .param("offset", offset)
                        .param("size", size)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        Map result = om.readValue(findWithOffsetSize.getResponse().getContentAsString(), Map.class);

        List<Resource> resources = (List<Resource>) result.get("resources");
        Integer totalCount = (Integer) result.get("totalCount");
        assertThat(totalCount).isGreaterThanOrEqualTo(resources.size());

        //모두 조회
        MvcResult findAll = mvc.perform(get(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        result = om.readValue(findAll.getResponse().getContentAsString(), Map.class);
        resources = (List<Resource>) result.get("resources");

        assertThat(resources.size()).isEqualTo(totalCount);
    }

    private static Stream offsetAndSizeValues(){
        return Stream.of(
                Arguments.of("0", "5")
        );
    }

    @DisplayName("Resource 삭제 테스트")
    @WithMockCustomUser(role = "ROLE_ADMIN")
    @Test
    void deleteTest() throws Exception{
        //when
        mvc.perform(delete(BASE_URL +"/"+id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

}
