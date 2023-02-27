package io.oauth2.client.board.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.oauth2.client.board.Board;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class BoardUpdateDto {
    private Long id;
    @NotBlank
    private String category;
    @NotBlank
    private String subject;
    @JsonProperty("resource_id")
    private Long resourceId;

    public static Board toBoard(BoardUpdateDto dto){
        return Board.builder()
                .id(dto.getId())
                .category(dto.getCategory())
                .subject(dto.getSubject())
                    .build();
    }
}
