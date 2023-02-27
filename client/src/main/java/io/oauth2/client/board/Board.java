package io.oauth2.client.board;

import io.oauth2.client.resource.Resource;
import io.oauth2.client.role.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
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
