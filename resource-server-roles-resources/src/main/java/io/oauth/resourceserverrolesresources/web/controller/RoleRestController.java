package io.oauth.resourceserverrolesresources.web.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.oauth.resourceserverrolesresources.service.RoleService;
import io.oauth.resourceserverrolesresources.web.domain.Role;
import io.oauth.resourceserverrolesresources.web.utils.ApiUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.List;

import static io.oauth.resourceserverrolesresources.web.utils.ApiUtils.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/roles")
public class RoleRestController {

    private static final String DEFAULT_PATH = "/api/roles/";
    private final RoleService roleService;
    private final ObjectMapper objectMapper;

    @PostMapping
    public ResponseEntity addRole(@RequestBody String role, @RequestParam("parent-id") String parentId) throws JsonProcessingException, URISyntaxException {
        Role roleForAdd = objectMapper.readValue(role, Role.class);
        roleService.addRole(roleForAdd, parentId);

        return successCreated(DEFAULT_PATH+roleForAdd.getId());
    }

    @PutMapping
    public ResponseEntity updateRole(@RequestBody String role, @RequestParam("parent-id") String id) throws JsonProcessingException, URISyntaxException {

        Role roleForUpdate = objectMapper.readValue(role, Role.class);

        roleService.updateRole(roleForUpdate, id);

        return successNoContent();
    }

    @PostMapping("/{roleId}/user/{userId}")
    public ResponseEntity addRoleOfUser(@PathVariable String roleId, @PathVariable String userId) throws JsonProcessingException {

        roleService.addRoleOfUser(roleId, userId);

        return successNoContent();
    }

    @PutMapping("/{roleId}/user/{userId}")
    public ResponseEntity updateRoleOfUser(@PathVariable String roleId, @PathVariable String userId) throws JsonProcessingException {

        roleService.updateRoleOfUser(roleId, userId);

        return successNoContent();
    }

    @GetMapping
    public ResponseEntity<String> getRoles() throws JsonProcessingException {
        List<Role> roles = roleService.findAll();

        String result = objectMapper.writeValueAsString(roles);

        return successOk(result, MediaType.APPLICATION_JSON);
    }

    @DeleteMapping("/{roleId}")
    public ResponseEntity<Void> deleteRole(@PathVariable String roleId){
        roleService.deleteRole(roleId);

        return successNoContent();
    }
}
