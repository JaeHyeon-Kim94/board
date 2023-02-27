package io.oauth2.client.role.dto;

import io.oauth2.client.role.Role;
import lombok.Data;

@Data
public class RoleRequestDto {
    private String id;
    private String description;
    private String name;
    private String parentId;

    public static Role toRole(RoleRequestDto dto){
        return Role.builder()
                .id(dto.getId())
                .description(dto.getDescription())
                .name(dto.getName())
                    .build();
    }
}
