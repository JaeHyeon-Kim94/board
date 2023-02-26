package io.oauth2.client.board;

import java.util.List;

public interface BoardRepository {

    Long insert(Board board, Long resourceId);

    int updateBoard(Board board, Long resourceId);

    int deleteBoard(Long boardId);

    Board findBoardById(Long boardId);

    List<Board> findBoards(Long offset, int size);

}
