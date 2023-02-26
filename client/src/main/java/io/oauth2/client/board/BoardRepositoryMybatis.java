package io.oauth2.client.board;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class BoardRepositoryMybatis implements BoardRepository {

    private final BoardMapper boardMapper;

    @Override
    public Long insert(Board board, Long resourceId) {
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
    public List<Board> findBoards(Long offset, int size) {
        return boardMapper.findBoards(offset, size);
    }
}
