package io.oauth.resourceserverrolesresources.web.controller;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.oauth.resourceserverrolesresources.service.RoleService;
import io.oauth.resourceserverrolesresources.web.domain.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("api/roles")
public class RoleRestController {

    private final RoleService roleService;
    private final ObjectMapper objectMapper;

    @PostMapping
    public ResponseEntity addRole(@RequestBody String role, @RequestParam("parent-id") String parentId) throws JsonProcessingException, URISyntaxException {
        Role roleForAdd = objectMapper.readValue(role, Role.class);
        roleService.addRole(roleForAdd, parentId);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(new URI("/api/roles/"+roleForAdd.getId()));

        return new ResponseEntity<>(null, httpHeaders, HttpStatus.ACCEPTED);
    }

    @PutMapping
    public ResponseEntity updateRole(@RequestBody String role, @RequestParam("parent-id") String id) throws JsonProcessingException, URISyntaxException {

        Role roleForUpdate = objectMapper.readValue(role, Role.class);

        roleService.updateRole(roleForUpdate, id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/{roleId}/user/{userId}")
    public ResponseEntity addRoleOfUser(@PathVariable String roleId, @PathVariable String userId) throws JsonProcessingException {

        roleService.addRoleOfUser(roleId, userId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{roleId}/user/{userId}")
    public ResponseEntity updateRoleOfUser(@PathVariable String roleId, @PathVariable String userId) throws JsonProcessingException {

        roleService.updateRoleOfUser(roleId, userId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping
    public ResponseEntity<String> getRoles() throws JsonProcessingException {
        List<Role> roles = roleService.getRoles();

        String s = objectMapper.writeValueAsString(roles);

        return new ResponseEntity(s, HttpStatus.OK);
    }

    @DeleteMapping("/{roleId}")
    public ResponseEntity deleteRole(@PathVariable String roleId){
        roleService.deleteRole(roleId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
