package io.oauth2.client.security.handler;

import io.oauth2.client.security.utils.CookieUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Component
public class JwtLogoutHandler implements LogoutHandler {


    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

        JwtAuthenticationToken token = (JwtAuthenticationToken) authentication;
        String tokenValue = null;

        if(token == null) {
            Cookie[] cookies = request.getCookies();
            if (cookies == null) {
                sendRedirect(response);
                return;
            }
        }

        Optional<Cookie> idt = CookieUtils.getCookie(request, "idt");
        Optional<Cookie> rId = CookieUtils.getCookie(request, "r_id");

        idt.ifPresent(cookie -> CookieUtils.deleteCookie(request, response, cookie.getName()));
        rId.ifPresent(cookie -> CookieUtils.deleteCookie(request, response, cookie.getName()));

        sendRedirect(response);
    }

    private void sendRedirect(HttpServletResponse response) {
        try{
            response.sendRedirect("/");
        } catch (IOException e){
            throw new RuntimeException("로그아웃 도중 오류 발생", e);
        }
    }
}
