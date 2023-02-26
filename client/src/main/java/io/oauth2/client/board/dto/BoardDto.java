package io.oauth2.client.board.dto;

import io.oauth2.client.board.Board;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Data
public class BoardDto {

    private Long id;
    @NotBlank
    private String category;
    @NotBlank
    @Length(max = 45)
    private String subject;

    private Long resourceId;
    private String roleId;

    public static Board toBoard(BoardDto dto){
        Board board = new Board();
        board.setId(dto.getId());
        board.setSubject(dto.getSubject());
        board.setCategory(dto.getCategory());
        return board;
    }

}
