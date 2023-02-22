package io.oauth.oauth2authorizationclientserver.web.controller;


import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.shaded.json.JSONObject;
import com.nimbusds.jose.util.Base64URL;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.OAuth2AuthorizationServerMetadataClaimNames;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.Serializable;
import java.util.*;

@RequiredArgsConstructor
@RestController
public class MetadataController {

    @Value("${authorization-server.jwks-uri}")
    private String jwksUri;

    @Value("${authorization-server.issuer-uri}")
    private String issuerUri;

    private final JWK jwk;

    @GetMapping("/.well-known/**")
    public ResponseEntity<Map<String, Object>> responseMetadata(){

        Map<String, Object> map = new HashMap<>();
        map.put(OAuth2AuthorizationServerMetadataClaimNames.JWKS_URI, jwksUri);
        map.put(OAuth2AuthorizationServerMetadataClaimNames.ISSUER, issuerUri);
        map.put("subject_types_supported", Set.of("public"));
        map.put(OAuth2AuthorizationServerMetadataClaimNames.TOKEN_ENDPOINT, "http://127.0.0.1:9001/token");
        map.put("id_token_signing_alg_values_supported", List.of("RS256"));


        HttpHeaders headers = new HttpHeaders();

        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @GetMapping("/oauth2/jwks")
    public ResponseEntity<Map<String, Object>> responseJwks(){
        RSAKey rsaKey = (RSAKey)jwk;

        Base64URL publicExponent = rsaKey.getPublicExponent();
        Base64URL modulus = rsaKey.getModulus();

        Map<String, Serializable> jwks = Map.of(
                "kty",   "RSA"
                ,   "e",        publicExponent.toString()
                ,   "kid",      rsaKey.getKeyID()
                ,   "n",        modulus.toString()
        );

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("keys", Arrays.asList(jwks));

        return new ResponseEntity<>(jsonObject, HttpStatus.OK);
    }

}
