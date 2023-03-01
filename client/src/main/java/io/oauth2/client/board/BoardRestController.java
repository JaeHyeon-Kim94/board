package io.oauth2.client.board;

import io.oauth2.client.board.dto.BoardCreateDto;
import io.oauth2.client.board.dto.BoardUpdateDto;
import io.oauth2.client.web.page.Pageable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.Map;

import static io.oauth2.client.web.utils.ApiUtils.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/boards")
public class BoardRestController {

    private static final String DEFAULT_PATH = "/api/boards/";
    private final BoardResourceRoleService boardResourceRoleService;
    private final BoardService boardService;

    @PostMapping
    public ResponseEntity<Void> addBoard(@RequestBody @Validated BoardCreateDto board) throws URISyntaxException {
        Long boardId = boardResourceRoleService.reloadRoleAndResourcesAfterAddBoard(board);
        return successCreated(DEFAULT_PATH+boardId);
    }

    @PutMapping("/{boardId}")
    public ResponseEntity<Void> updateBoard(@RequestBody @Validated BoardUpdateDto board, @PathVariable Long boardId) {
        boardResourceRoleService.reloadRoleAndResourcesAfterUpdateBoard(board, boardId);

        return successNoContent();
    }

    @DeleteMapping("/{boardId}")
    public ResponseEntity<Void> deleteBoard(@PathVariable Long boardId){
        boardResourceRoleService.reloadRoleAndResourcesAfterDeleteBoard(boardId);

        return successNoContent();
    }

    @GetMapping("/{boardId}")
    public ResponseEntity<Board> findByBoardId(@PathVariable Long boardId) {
        Board board = boardService.findByBoardId(boardId);

        return successOk(board);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> findBoards(Pageable pageable) {
        Map<String, Object> result;
        if(pageable == null){
            result = Map.of("boards", boardService.findAll());
        }else {
            result = boardService.findBoards(pageable.getOffset(), pageable.getSize());
        }

        return successOk(result);
    }


    //TODO
    @GetMapping("/{category}/{subject}")
    public ResponseEntity<String> posts(@PathVariable String category, @PathVariable String subject, Pageable pageable){
        return successOk("TODO posts(can access everyone)");
    }

    //TODO
    @PostMapping("/{category}/{subject}/management")
    public ResponseEntity<String> manageBoard(@PathVariable String category, @PathVariable String subject, @RequestBody String manageContent){
        return successOk(manageContent);
    }

}
