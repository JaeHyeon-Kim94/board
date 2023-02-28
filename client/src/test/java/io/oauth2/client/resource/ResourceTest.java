package io.oauth2.client.resource;

import io.oauth2.client.BaseTest;
import io.oauth2.client.resource.dto.ResourceRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ResourceTest extends BaseTest {
    ResourceRequestDto requestDto;
    String url = "/api/resources";
    Long id;

    @MockBean
    ResourceService resourceService;

    @BeforeEach
    void before() throws Exception {
        requestDto = ResourceRequestDto.builder()
                .type("url")
                .level(1111L)
                .roleId("U_0000")
                .value("/api/test")
                .httpMethod(null)
                .build();


        String resource = om.writeValueAsString(requestDto);

        mvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(resource))
                .andExpect(result ->
                        id = Long.valueOf(result.getResponse()
                                .getHeader("Location")
                                .replace("/api/resources/", "")));
    }


    @Test
    @DisplayName("Resource 수정 테스트")
    void updateTest() throws Exception {
        //given
        ResourceRequestDto dto = ResourceRequestDto.builder()
                .type("modified Type")
                .level(999L)
                .roleId("M_0000")
                .value("modified Value")
                .build();

        //when
        mvc.perform(put(url+"/"+id)
                .content(om.writeValueAsBytes(dto))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Resource 한 건 조회 테스트")
    void selectOneTest() throws Exception{
        //given
        requestDto.setId(id);

        //when
        MvcResult mvcResult = mvc.perform(get(url + "/" + id)
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

    @ParameterizedTest
    @MethodSource("offsetAndSizeValues")
    @DisplayName("Resource 리스트 조회 테스트")
    void selectList(long offset, int size) throws Exception{
        //when
        MvcResult findWithOffsetSize = mvc.perform(get(url+"?offset="+offset+"&size="+size)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        Map result = om.readValue(findWithOffsetSize.getResponse().getContentAsString(), Map.class);

        List<Resource> resources = (List<Resource>) result.get("resources");
        Integer totalCount = (Integer) result.get("totalCount");
        assertThat(totalCount).isGreaterThanOrEqualTo(resources.size());

        //모두 조회
        MvcResult findAll = mvc.perform(get(url)
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
                Arguments.of(0L, 5)
        );
    }

    @Test
    @DisplayName("Resource 삭제 테스트")
    void deleteTest() throws Exception{
        //when
        mvc.perform(delete(url+"/"+id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

}
