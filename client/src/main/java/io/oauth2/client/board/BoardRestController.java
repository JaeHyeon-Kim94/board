package io.oauth2.client.board;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.oauth2.client.board.dto.BoardDto;
import io.oauth2.client.web.page.Pageable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.List;

import static io.oauth2.client.web.utils.ApiUtils.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/boards")
public class BoardRestController {

    private static final String DEFAULT_PATH = "/api/boards/";
    private final ObjectMapper objectMapper;
    private final BoardService boardService;

    @PostMapping
    public ResponseEntity<Void> addBoard(@RequestBody String board) throws JsonProcessingException, URISyntaxException {
        BoardDto dto = objectMapper.readValue(board, BoardDto.class);
        Long boardId = boardService.addBoard(BoardDto.toBoard(dto), dto.getResourceId());

        return successCreated(DEFAULT_PATH+boardId);
    }

    @PutMapping("/{boardId}")
    public ResponseEntity<Void> updateBoard(@RequestBody String board, @PathVariable Long boardId) throws JsonProcessingException {
        BoardDto dto = objectMapper.readValue(board, BoardDto.class);
        dto.setId(boardId);
        boardService.updateBoard(BoardDto.toBoard(dto), dto.getResourceId());

        return successNoContent();
    }

    @DeleteMapping("/{boardId}")
    public ResponseEntity<Void> deleteBoard(@PathVariable Long boardId){
        boardService.deleteBoard(boardId);

        return successNoContent();
    }

    @GetMapping("/{boardId}")
    public ResponseEntity<String> findByBoardId(@PathVariable Long boardId) throws JsonProcessingException {
        Board board = boardService.findByBoardId(boardId);

        return successOk(objectMapper.writeValueAsString(board));
    }

    @GetMapping
    public ResponseEntity<String> findBoards(Pageable pageable) throws JsonProcessingException {
        List<Board> boards = boardService.findBoards(pageable.getOffset(), pageable.getSize());

        return successOk(objectMapper.writeValueAsString(boards));
    }
}
