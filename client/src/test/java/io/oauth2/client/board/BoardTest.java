package io.oauth2.client.board;

import com.nimbusds.jose.util.JSONObjectUtils;
import io.oauth2.client.BaseTest;
import io.oauth2.client.board.dto.BoardCreateDto;
import io.oauth2.client.board.dto.BoardUpdateDto;
import io.oauth2.client.resource.Resource;
import io.oauth2.client.role.Role;
import io.oauth2.client.security.WithMockCustomUser;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@AutoConfigureMockMvc
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
public class BoardTest extends BaseTest {

    private static final String BASE_URL = "/api/boards";

    private BoardCreateDto boardCreateDto;
    private Long id;

    @DisplayName("@BeforeAll - Board 추가 테스트 - 성공")
    @BeforeEach
    void beforeEach() throws Exception {
        //given
        BoardCreateDto.BoardRole boardRole
                = BoardCreateDto.BoardRole.builder()
                .id("M_TEST_BOARD_CATEGORY_SUBJECT")
                .name("ROLE_TEST_BOARD_CATEGORY_SUBJECT")
                .description("테스트 게시판 (카테고리 - 주제)")
                .parentId("M_0000")
                .build();

        BoardCreateDto.BoardResource boardResource
                = BoardCreateDto.BoardResource.builder()
                .type("url")
                .level(4100L)
                .build();

        boardCreateDto
                = BoardCreateDto.builder()
                .userId("test_manager")
                .category("test-category")
                .subject("test-subject")
                .role(boardRole)
                .resource(boardResource)
                .build();

        JwtAuthenticationToken token = getAuthentication("test_manager", "ROLE_ADMIN");

        String dto = om.writeValueAsString(boardCreateDto);

        //when
        MvcResult mvcResult = mvc.perform(post(BASE_URL)
                        .with(authentication(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(dto))
                .andExpect(status().isCreated())
                .andReturn();

        String location = mvcResult.getResponse().getHeader("Location");
        id = Long.valueOf(location.replace(BASE_URL + "/", ""));
    }

    //add
    @DisplayName("Board 추가 테스트 - conflict 실패")
    @WithMockCustomUser(role = "ROLE_ADMIN")
    @Order(1)
    @Test
    void insertTest() throws Exception {
        String dto = om.writeValueAsString(boardCreateDto);
        mvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(dto))
                .andExpect(status().isConflict());
    }

    //Board 추가와 함께 설정된 권한처리 테스트
    @DisplayName("Board 권한 테스트")
    @Order(2)
    @Test
    void checkAuthority() throws Exception {
        //given
        String content = "{\"manageContent\" : \"TODO MANAGE CONTENT...\"}";


        JwtAuthenticationToken token = getAuthentication("test_manager", "ROLE_TEST_BOARD_CATEGORY_SUBJECT");

        MvcResult mvcResult = mvc.perform(post(BASE_URL + "/test-category/test-subject/management")
                        .with(authentication(token))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk())
                .andReturn();

        Map<String, Object> parsedResult = JSONObjectUtils.parse(mvcResult.getResponse().getContentAsString());
        assertThat((String)parsedResult.get("manageContent")).isEqualTo("TODO MANAGE CONTENT...");
    }



    @DisplayName("Board 수정 테스트")
    @WithMockCustomUser(role = "ROLE_ADMIN")
    @Order(3)
    @Test
    void updateTest() throws Exception {

        BoardUpdateDto dto = BoardUpdateDto.builder()
                .category("test-category-modified")
                .subject("test-subject-modified")
                .build();

        mvc.perform(put(BASE_URL +"/"+id)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(dto)))
                .andExpect(status().isNoContent());
    }

    @DisplayName("Board 한 건 조회 테스트")
    @WithMockCustomUser(role = "ROLE_ADMIN")
    @Test
    void selectOneTest() throws Exception {
        MvcResult mvcResult = mvc.perform(get(BASE_URL + "/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        Board foundBoard = om.readValue(mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8), Board.class);

        Resource resource = foundBoard.getResource();
        Role role = foundBoard.getRole();

        Board insertedBoard = BoardCreateDto.toBoard(boardCreateDto);

        assertThat(foundBoard.getCategory()).isEqualTo(insertedBoard.getCategory());
        assertThat(foundBoard.getSubject()).isEqualTo(insertedBoard.getSubject());
        assertThat(resource.getType()).isEqualTo(insertedBoard.getResource().getType());
        assertThat(resource.getLevel()).isEqualTo(insertedBoard.getResource().getLevel());
        assertThat(resource.getValue()).isEqualTo(BASE_URL +"/"+insertedBoard.getCategory()+"/"+insertedBoard.getSubject()+"/management");
        assertThat(resource.getHttpMethod()).isEqualTo(insertedBoard.getResource().getHttpMethod());
        assertThat(role.getDescription()).isEqualTo(insertedBoard.getRole().getDescription());
        assertThat(role.getName()).isEqualTo(insertedBoard.getRole().getName());
    }

    @DisplayName("Board 리스트 조회 테스트")
    @WithMockCustomUser(role = "ROLE_ADMIN")
    @MethodSource("offsetAndSizeValues")
    @ParameterizedTest
    void selectListTest(String offset, String size) throws Exception {
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

        List<Board> boards = (List<Board>) result.get("boards");
        Integer totalCount = (Integer) result.get("totalCount");
        assertThat(totalCount).isGreaterThanOrEqualTo(boards.size());

        MvcResult findAll = mvc.perform(get(BASE_URL + "/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andReturn();

        result = om.readValue(findAll.getResponse().getContentAsString(), Map.class);
        boards = (List<Board>) result.get("boards");

        assertThat(boards.size()).isEqualTo(totalCount);
    }
    private static Stream offsetAndSizeValues(){
        return Stream.of(
                Arguments.of("0", "5")
        );
    }

    @DisplayName("Board 리스트 삭제 테스트")
    @WithMockCustomUser(role = "ROLE_ADMIN")
    @Order(4)
    @Test
    void deleteTest() throws Exception {

        mvc.perform(delete(BASE_URL+"/"+id)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

    }


    private JwtAuthenticationToken getAuthentication(String sub, String authority){
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("sub", sub)
                .claim("authorities", authority)
                .build();

        return new JwtAuthenticationToken(jwt, Set.of(new SimpleGrantedAuthority(authority)));
    }
}
