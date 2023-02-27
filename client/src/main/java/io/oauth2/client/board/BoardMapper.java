package io.oauth2.client.board;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BoardMapper {

    int insert(Board board, Long resourceId);

    int updateBoard(Board board, Long resourceId);

    int deleteBoard(Long boardId);

    Board findBoardById(Long boardId);

    List<Board> findBoards(Long offset, int size);

    List<Board> findAll();

    Long findBoardsCount();
}
