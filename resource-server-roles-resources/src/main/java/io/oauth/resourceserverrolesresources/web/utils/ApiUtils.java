package io.oauth.resourceserverrolesresources.web.utils;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.net.URISyntaxException;

public class ApiUtils {



    public static ResponseEntity<Void> successNoContent(){
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    public static ResponseEntity<Void> successCreated(String location) throws URISyntaxException {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(new URI(location));

        return new ResponseEntity<>(null, httpHeaders, HttpStatus.CREATED);
    }

    public static <T> ResponseEntity<T> successOk(T body, MediaType type){

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(type);

        return new ResponseEntity<>(body, httpHeaders, HttpStatus.OK);
    }

}
