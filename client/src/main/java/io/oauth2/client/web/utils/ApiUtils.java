package io.oauth2.client.web.utils;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.net.URISyntaxException;

public class ApiUtils {

    public static ResponseEntity<Void> successNoContent(){
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(httpHeaders, HttpStatus.NO_CONTENT);
    }

    public static ResponseEntity<Void> successCreated(String location) throws URISyntaxException {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setLocation(new URI(location));

        return new ResponseEntity<>(null, httpHeaders, HttpStatus.CREATED);
    }

    public static <T> ResponseEntity<T> successOk(T body){

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(body, httpHeaders, HttpStatus.OK);
    }

    public static ResponseEntity<Void> redirectSeeOther(String location) throws URISyntaxException {

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setLocation(new URI(location));
        return new ResponseEntity<Void>(httpHeaders, HttpStatus.SEE_OTHER);
    }

}
