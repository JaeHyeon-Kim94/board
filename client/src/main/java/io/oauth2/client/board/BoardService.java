package io.oauth2.client.board;

import io.oauth2.client.board.dto.BoardCreateDto;
import io.oauth2.client.board.dto.BoardUpdateDto;
import io.oauth2.client.resource.Resource;
import io.oauth2.client.resource.ResourceRepository;
import io.oauth2.client.role.Role;
import io.oauth2.client.role.RoleRepository;
import io.oauth2.client.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class BoardService {

    private final BoardRepository boardRepository;
    private final ResourceRepository resourceRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    @Transactional
    public Long addBoard(BoardCreateDto dto){

        Role role = BoardCreateDto.BoardRole.toRole(dto.getRole());
        roleRepository.addRole(role, dto.getRole().getParentId());

        userRepository.addUserRole(dto.getUserId(), role.getId());

        Resource resource = BoardCreateDto.BoardResource.toResource(dto.getResource());
        resource.setValue("/api/boards/"+dto.getCategory()+"/"+dto.getSubject()+"/management");
        resourceRepository.addResource(resource, role.getId());

        Board board = BoardCreateDto.toBoard(dto);
        boardRepository.addBoard(board, resource.getId());

        return board.getId();
    }

    @Transactional
    public int updateBoard(BoardUpdateDto dto){
        Board board = BoardUpdateDto.toBoard(dto);
        return boardRepository.updateBoard(board, dto.getResourceId());
    }

    @Transactional
    public int deleteBoard(Long boardId){
        Board board = boardRepository.findBoardById(boardId);
        Resource resource = board.getResource();
        if(resource!=null){
            resourceRepository.deleteResource(resource.getId());
            if(resource.getRole() != null){
                roleRepository.deleteRole(resource.getRole().getId());
            }
        }

        return boardRepository.deleteBoard(boardId);
    }

    @Transactional(readOnly = true)
    public Board findByBoardId(Long boardId){
        Board board = boardRepository.findBoardById(boardId);
        return board;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> findBoards(Long offset, int size){
        return boardRepository.findBoards(offset, size);
    }

    @Transactional(readOnly = true)
    public List<Board> findAll(){
        return boardRepository.findAll();
    }

}
