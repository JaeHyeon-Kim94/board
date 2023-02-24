package io.oauth.resourceserverrolesresources.web.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.oauth.resourceserverrolesresources.service.UserService;
import io.oauth.resourceserverrolesresources.web.domain.User;
import io.oauth.resourceserverrolesresources.web.utils.ApiUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static io.oauth.resourceserverrolesresources.web.utils.ApiUtils.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserRestController {

    private final UserService userService;
    private final ObjectMapper objectMapper;

    @GetMapping
    public ResponseEntity<String> getUsers() throws JsonProcessingException {
        List<User> users = userService.findAll();

        String result = objectMapper.writeValueAsString(users);

        return successOk(result, MediaType.APPLICATION_JSON);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<String> getUser(@PathVariable String userId) throws JsonProcessingException {
        User user = userService.findById(userId);

        String result = objectMapper.writeValueAsString(user);

        return successOk(result, MediaType.APPLICATION_JSON);
    }

}
