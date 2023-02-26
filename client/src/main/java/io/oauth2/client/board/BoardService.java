package io.oauth2.client.board;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class BoardService {

    private final BoardRepository boardRepository;

    @Transactional
    public Long addBoard(Board board, Long resourceId){
        return boardRepository.insert(board, resourceId);
    }

    @Transactional
    public int updateBoard(Board board, Long resourceId){
        return boardRepository.updateBoard(board, resourceId);
    }

    @Transactional
    public int deleteBoard(Long boardId){
        return boardRepository.deleteBoard(boardId);
    }

    @Transactional
    public Board findByBoardId(Long boardId){
        return boardRepository.findBoardById(boardId);
    }

    @Transactional
    public List<Board> findBoards(Long offset, int size){
        return boardRepository.findBoards(offset, size);
    }

}
