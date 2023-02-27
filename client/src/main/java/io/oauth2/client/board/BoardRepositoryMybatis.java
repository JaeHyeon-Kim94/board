package io.oauth2.client.board;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Repository
public class BoardRepositoryMybatis implements BoardRepository {

    private final BoardMapper boardMapper;

    @Override
    public Long addBoard(Board board, Long resourceId) {
        boardMapper.insert(board, resourceId);
        return board.getId();
    }

    @Override
    public int updateBoard(Board board, Long resourceId) {
        return boardMapper.updateBoard(board, resourceId);
    }

    @Override
    public int deleteBoard(Long boardId) {
        return boardMapper.deleteBoard(boardId);
    }

    @Override
    public Board findBoardById(Long boardId) {
        return boardMapper.findBoardById(boardId);
    }

    @Override
    public Map<String, Object> findBoards(Long offset, int size) {
        List<Board> boards = boardMapper.findBoards(offset, size);
        Long totalCount = boardMapper.findBoardsCount();
        return Map.of("boards", boards, "totalCount", totalCount);
    }

    @Override
    public List<Board> findAll() {
        return boardMapper.findAll();
    }
}
