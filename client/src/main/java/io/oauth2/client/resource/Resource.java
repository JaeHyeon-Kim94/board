package io.oauth2.client.resource;

import io.oauth2.client.role.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Resource {

    private Long id;
    private String type;
    private Long level;
    private String value;
    private String httpMethod;
    private Role role;
}
