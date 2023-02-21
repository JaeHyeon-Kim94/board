package io.oauth2.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Slf4j
@Controller
public class IndexController {

    @GetMapping("/login/{provider}")
    public void login(HttpServletRequest request, HttpServletResponse response, @PathVariable String provider) throws IOException {
        RestTemplate restTemplate = new RestTemplate();

        response.sendRedirect("http://127.0.0.1:9001/oauth2/authorization/"+provider);

    }

    @GetMapping("/token")
    public void token(@RequestParam("id-token") String idToken
                      , @RequestParam("access-token") String accessToken
                      , @RequestParam("token-type") String tokenType
                      ){

        log.info("id Token : {}", idToken);
        log.info("access Token : {}", accessToken);
        log.info("token type : {}", tokenType);

    }

}
