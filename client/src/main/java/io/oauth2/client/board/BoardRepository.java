package io.oauth2.client.board;

import java.util.List;
import java.util.Map;

public interface BoardRepository {

    Long addBoard(Board board, Long resourceId);

    int updateBoard(Board board, Long resourceId);

    int deleteBoard(Long boardId);

    Board findBoardById(Long boardId);

    Map<String, Object> findBoards(Long offset, int size);

    List<Board> findAll();

}
