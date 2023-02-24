package io.oauth.resourceserverrolesresources.web.domain;

import lombok.Data;

@Data
public class Role {


    private String id;
    private String description;
    private String name;
    private Role parent;

}
