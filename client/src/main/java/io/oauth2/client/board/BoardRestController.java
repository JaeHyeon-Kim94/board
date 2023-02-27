package io.oauth2.client.board;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.oauth2.client.board.dto.BoardCreateDto;
import io.oauth2.client.board.dto.BoardUpdateDto;
import io.oauth2.client.web.page.Pageable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

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
    public ResponseEntity<Void> addBoard(@RequestBody @Validated BoardCreateDto board) throws JsonProcessingException, URISyntaxException {
        Long boardId = boardService.addBoard(board);
        return successCreated(DEFAULT_PATH+boardId);
    }

    @PutMapping("/{boardId}")
    public ResponseEntity<Void> updateBoard(@RequestBody @Validated BoardUpdateDto board, @PathVariable Long boardId) throws JsonProcessingException {
        boardService.updateBoard(board);

        return successNoContent();
    }

    @DeleteMapping("/{boardId}")
    public ResponseEntity<Void> deleteBoard(@PathVariable Long boardId){
        boardService.deleteBoard(boardId);

        return successNoContent();
    }

    @GetMapping("/{boardId}")
    public ResponseEntity<Board> findByBoardId(@PathVariable Long boardId) throws JsonProcessingException {
        Board board = boardService.findByBoardId(boardId);

        return successOk(board);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> findBoards(Pageable pageable) throws JsonProcessingException {
        Map<String, Object> result = null;
        if(pageable == null){
            result = Map.of("boards", boardService.findAll());
        }else {
            result = boardService.findBoards(pageable.getOffset(), pageable.getSize());
        }

        return successOk(result);
    }
}
