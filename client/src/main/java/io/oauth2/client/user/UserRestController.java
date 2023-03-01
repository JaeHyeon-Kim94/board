package io.oauth2.client.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.oauth2.client.web.page.Pageable;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static io.oauth2.client.web.utils.ApiUtils.successNoContent;
import static io.oauth2.client.web.utils.ApiUtils.successOk;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/users")
public class UserRestController {

    private final UserService userService;
    private final ObjectMapper objectMapper;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getUsers(Pageable pageable) throws JsonProcessingException {
        Map<String, Object> result = null;
        if(pageable == null){
            result = Map.of("users", userService.findAll());
        }else {
            result = userService.findUsers(pageable.getOffset(), pageable.getSize());
        }

        return successOk(result);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<String> getUser(@PathVariable String userId) throws JsonProcessingException {
        User user = userService.findById(userId);

        String result = objectMapper.writeValueAsString(user);

        return successOk(result);
    }

    @PostMapping("/{userId}/roles/{roleId}")
    public ResponseEntity<Void> addRoleOfUser(@PathVariable String roleId, @PathVariable String userId) {

        userService.addUserRole(userId, roleId);

        return successNoContent();
    }

    @DeleteMapping("/{userId}/roles/{roleId}")
    public ResponseEntity<Void> deleteRoleOfUser(@PathVariable String roleId, @PathVariable String userId) {

        userService.deleteUserRole(roleId, userId);

        return successNoContent();
    }

}
