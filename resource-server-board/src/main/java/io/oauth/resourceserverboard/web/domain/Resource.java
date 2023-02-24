package io.oauth.resourceserverboard.web.domain;

import lombok.Data;

import java.util.Set;

@Data
public class Resource {

    private Long id;
    private String type;
    private String level;
    private String value;
    private String httpMethod;
    private Role role;
}
