package io.oauth2.client.resource;

import io.oauth2.client.role.Role;
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
