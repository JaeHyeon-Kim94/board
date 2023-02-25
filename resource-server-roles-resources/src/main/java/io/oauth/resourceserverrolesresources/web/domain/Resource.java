package io.oauth.resourceserverrolesresources.web.domain;

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
