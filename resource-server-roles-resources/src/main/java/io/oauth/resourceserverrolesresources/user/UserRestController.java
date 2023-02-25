package io.oauth.resourceserverrolesresources.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.oauth.resourceserverrolesresources.web.page.Pageable;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static io.oauth.resourceserverrolesresources.web.utils.ApiUtils.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserRestController {

    private final UserService userService;
    private final ObjectMapper objectMapper;

    @GetMapping
    public ResponseEntity<String> getUsers(Pageable pageable) throws JsonProcessingException {
        String result = null;
        if(pageable == null){
            List<User> users = userService.findAll();
            result = objectMapper.writeValueAsString(users);
        }else {
            Map<String, Object> usersWithTotalCount = userService.findUsers(pageable.getOffset(), pageable.getSize());
            result = objectMapper.writeValueAsString(usersWithTotalCount);
        }

        return successOk(result, MediaType.APPLICATION_JSON);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<String> getUser(@PathVariable String userId) throws JsonProcessingException {
        User user = userService.findById(userId);

        String result = objectMapper.writeValueAsString(user);

        return successOk(result, MediaType.APPLICATION_JSON);
    }

    @PostMapping("/{userId}/roles/{roleId}")
    public ResponseEntity addRoleOfUser(@PathVariable String roleId, @PathVariable String userId) throws JsonProcessingException {

        userService.addUserRole(roleId, userId);

        return successNoContent();
    }

    @PutMapping("/{userId}/roles/{roleId}")
    public ResponseEntity updateRoleOfUser(@PathVariable String roleId, @PathVariable String userId) throws JsonProcessingException {

        userService.updateUserRole(roleId, userId);

        return successNoContent();
    }

}