package io.oauth.oauth2authorizationclientserver.web.controller;

import io.oauth.oauth2authorizationclientserver.web.dto.RefreshTokenDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Slf4j
@RestController
public class TokenController {

    @PostMapping(value = "/oauth2/token"
            , consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE
            , produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> oauth2Token(@RequestParam("refresh_token") String refreshToken,
                                             @RequestParam("grant_type") String grantType,
                                             @RequestHeader MultiValueMap<String, Object> headers){




        return null;
    }

}
