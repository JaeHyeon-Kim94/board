package io.oauth.oauth2authorizationclientserver.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.crypto.impl.RSAKeyUtils;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import io.oauth.oauth2authorizationclientserver.security.common.JwtGenerator;
import io.oauth.oauth2authorizationclientserver.security.signer.RSASecuritySigner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.crypto.keygen.Base64StringKeyGenerator;
import org.springframework.security.crypto.keygen.StringKeyGenerator;

import java.io.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.RSAPrivateKeySpec;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Configuration
public class TokenConfig {

    @Bean
    public JwtGenerator jwtGenerator() throws JOSEException, IOException {
        return new JwtGenerator(rsaSecuritySigner(), rsaKey());
    }

    @Bean
    public StringKeyGenerator refreshTokenGenerator(){
        return new Base64StringKeyGenerator(Base64.getUrlEncoder().withoutPadding(), 96);
    }

    @Bean
    public RSASecuritySigner rsaSecuritySigner() {
        return new RSASecuritySigner();
    }
    @Bean
    public RSAKey rsaKey() throws JOSEException, IOException {
        String keyId = UUID.randomUUID().toString();
        RSAKey rsaKey = new RSAKeyGenerator(2048)
                .keyID(keyId)
                .algorithm(JWSAlgorithm.RS256)
                .generate();

        return rsaKey;
    }
}



