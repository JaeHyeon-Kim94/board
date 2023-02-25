package io.oauth.resourceserverrolesresources.role;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.oauth.resourceserverrolesresources.web.page.Pageable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import static io.oauth.resourceserverrolesresources.web.utils.ApiUtils.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/roles")
public class RoleRestController {

    private static final String DEFAULT_PATH = "/api/roles/";
    private final RoleService roleService;
    private final ObjectMapper objectMapper;

    @PutMapping("/{roleId}")
    public ResponseEntity updateRole(@RequestBody String role) throws JsonProcessingException, URISyntaxException {

        RolePutDto rolePutDto = objectMapper.readValue(role, RolePutDto.class);

        boolean created = roleService.putRole(rolePutDto);

        //계층구조 재설정.
        roleService.setRoleHierarchy();

        return created ? successCreated(DEFAULT_PATH+rolePutDto.getId()) : successNoContent();
    }

    @GetMapping
    public ResponseEntity<String> getRoles(Pageable pageable) throws JsonProcessingException {
        String result = null;
        if(pageable == null){
            List<Role> roles = roleService.findAll();
            result = objectMapper.writeValueAsString(roles);
        } else {
            Map<String, Object> rolesWithTotalCount = roleService.findRoles(pageable.getOffset(), pageable.getSize());
            result = objectMapper.writeValueAsString(rolesWithTotalCount);
        }

        return successOk(result, MediaType.APPLICATION_JSON);
    }

    @DeleteMapping("/{roleId}")
    public ResponseEntity<Void> deleteRole(@PathVariable String roleId){
        roleService.deleteRole(roleId);
        //계층구조 재설정.
        roleService.setRoleHierarchy();

        return successNoContent();
    }
}
