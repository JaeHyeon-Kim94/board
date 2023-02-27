package io.oauth2.client.role;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.oauth2.client.role.dto.RoleRequestDto;
import io.oauth2.client.web.page.Pageable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import static io.oauth2.client.web.utils.ApiUtils.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/roles")
public class RoleRestController {

    private static final String DEFAULT_PATH = "/api/roles/";
    private final RoleService roleService;
    private final ObjectMapper objectMapper;

    @PutMapping("/{roleId}")
    public ResponseEntity<Void> updateRole(@RequestBody RoleRequestDto dto, @PathVariable String roleId) throws JsonProcessingException, URISyntaxException {
        dto.setId(roleId);
        boolean created = roleService.putRole(dto);

        return created ? successCreated(DEFAULT_PATH+ dto.getId()) : successNoContent();
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getRoles(Pageable pageable) throws JsonProcessingException {
        Map<String, Object> result = null;
        if(pageable == null){
            result = Map.of("roles", roleService.findAll());
        } else {
            result = roleService.findRoles(pageable.getOffset(), pageable.getSize());
        }

        return successOk(result);
    }

    @GetMapping("/{roleId}")
    public ResponseEntity<Role> getRole(@PathVariable String roleId){
        Role role = roleService.findByid(roleId);
        return successOk(role);
    }

    @DeleteMapping("/{roleId}")
    public ResponseEntity<Void> deleteRole(@PathVariable String roleId){
        roleService.deleteRole(roleId);

        return successNoContent();
    }
}
