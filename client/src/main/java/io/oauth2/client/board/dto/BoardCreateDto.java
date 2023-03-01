package io.oauth2.client.board.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.oauth2.client.board.Board;
import io.oauth2.client.resource.Resource;
import io.oauth2.client.role.Role;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class BoardCreateDto {
    @NotBlank
    @Length(max = 45)
    private String category;
    @NotBlank
    @Length(max = 45)
    private String subject;
    @NotNull
    private BoardResource resource;
    @NotNull
    private BoardRole role;
    @JsonProperty("user_id")
    private String userId;

    public static Board toBoard(BoardCreateDto dto){
        return Board.builder()
                .category(dto.getCategory())
                .subject(dto.getSubject())
                        .build();
    }


    @Data
    public static class BoardResource{
        @NotBlank
        private String type;
        @NotNull
        private Long level;
        @JsonProperty("http_method")
        private String httpMethod;

        public static Resource toResource(BoardResource dto){
            return Resource.builder()
                    .type(dto.getType())
                    .level(dto.getLevel())
                    .httpMethod(dto.getHttpMethod())
                    .build();
        }
    }

    @Data
    public static class BoardRole{
        private String id;
        @JsonProperty("parent_id")
        private String parentId;
        private String description;
        private String name;

        public static Role toRole(BoardRole dto){
            return Role.builder()
                    .id(dto.getId())
                    .description(dto.getDescription())
                    .name(dto.getName())
                    .build();
        }
    }
}
