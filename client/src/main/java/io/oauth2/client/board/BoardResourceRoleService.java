package io.oauth2.client.board;

import io.oauth2.client.board.dto.BoardCreateDto;
import io.oauth2.client.board.dto.BoardUpdateDto;
import io.oauth2.client.role.RoleService;
import io.oauth2.client.security.metadatasource.UrlFilterInvocationSecurityMetadataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class BoardResourceRoleService {

    private final UrlFilterInvocationSecurityMetadataSource urlFilterInvocationSecurityMetadataSource;
    private final RoleService roleService;
    private final BoardService boardService;


    @Transactional
    public Long reloadRoleAndResourcesAfterAddBoard(BoardCreateDto dto){
        Long boardId = boardService.addBoard(dto);
        roleService.setRoleHierarchy();
        urlFilterInvocationSecurityMetadataSource.reload();
        return boardId;
    }

    @Transactional
    public void reloadRoleAndResourcesAfterUpdateBoard(BoardUpdateDto dto, Long boardId){
        boardService.updateBoard(dto, boardId);
        roleService.setRoleHierarchy();
        urlFilterInvocationSecurityMetadataSource.reload();
    }

    @Transactional
    public int reloadRoleAndResourcesAfterDeleteBoard(Long boardId){
        int result = boardService.deleteBoard(boardId);
        roleService.setRoleHierarchy();
        urlFilterInvocationSecurityMetadataSource.reload();
        return result;
    }

}
