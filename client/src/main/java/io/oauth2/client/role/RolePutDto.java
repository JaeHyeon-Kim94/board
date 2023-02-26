package io.oauth2.client.role;

import lombok.Data;

@Data
public class RolePutDto {
    private String id;
    private String description;
    private String name;
    private String parentId;

    public static Role toRole(RolePutDto dto){
        Role role = new Role();
        role.setId(dto.getId());
        role.setDescription(dto.getDescription());
        role.setName(dto.getName());
        return role;
    }
}
