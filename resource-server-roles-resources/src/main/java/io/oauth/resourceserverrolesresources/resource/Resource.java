package io.oauth.resourceserverrolesresources.resource;

import io.oauth.resourceserverrolesresources.role.Role;
import lombok.Data;

@Data
public class Resource {

    private Long id;
    private String type;
    private Long level;
    private String value;
    private String httpMethod;
    private Role role;
}
