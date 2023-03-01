package io.oauth2.client.board.dto;

import io.oauth2.client.board.Board;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class BoardUpdateDto {
    @NotBlank
    @Length(max = 45)
    private String category;
    @NotBlank
    @Length(max = 45)
    private String subject;
    private Long resourceId;

    public static Board toBoard(BoardUpdateDto dto){
        return Board.builder()
                .category(dto.getCategory())
                .subject(dto.getSubject())
                        .build();
    }

}
