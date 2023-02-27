package io.oauth2.client.role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Role {

    private String id;
    private String description;
    private String name;
    private Role parent;

}
