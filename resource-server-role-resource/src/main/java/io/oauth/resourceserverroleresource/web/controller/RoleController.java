package io.oauth.resourceserverroleresource.web.controller;

import io.oauth.resourceserverroleresource.web.domain.Role;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
public class RoleController {

    @GetMapping("/roles")
    public ResponseEntity<Map<String, Object>> responseRoles(HttpServletRequest request, HttpServletResponse response){

        return new ResponseEntity<>(Map.of("U_0000", new Role()), HttpStatus.OK);
    }

    @PostMapping("/roles")
    public ResponseEntity<String> responseRolePost(HttpServletRequest request, HttpServletResponse response){
        return new ResponseEntity<>("You are Admin", HttpStatus.OK);
    }

}
