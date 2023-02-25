package io.oauth.resourceserverrolesresources.role;

import lombok.Data;

@Data
public class Role {


    private String id;
    private String description;
    private String name;
    private Role parent;

}
