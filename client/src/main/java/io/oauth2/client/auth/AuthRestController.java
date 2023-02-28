package io.oauth2.client.auth;

import io.oauth2.client.web.utils.ApiUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;

@Slf4j
@RestController
@RequestMapping("/api/oauth")
public class AuthRestController {

    private static final String AUTH_URI = "http://127.0.0.1:9001/oauth2/authorization/";

    @GetMapping("/{providerId}")
    public ResponseEntity<Void> oauth2Login(@PathVariable String providerId) throws URISyntaxException {
        return ApiUtils.redirectSeeOther(AUTH_URI+providerId);
    }

}
