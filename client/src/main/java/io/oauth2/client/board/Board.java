package io.oauth2.client.board;

import io.oauth2.client.resource.Resource;
import io.oauth2.client.role.Role;
import lombok.Data;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Data
public class Board {

    private Long id;
    private String category;
    private String subject;
    private Resource resource;
    private Role role;
    private LocalDateTime regDate;
    private Timestamp modDate;

}
