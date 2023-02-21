package io.oauth.oauth2authorizationclientserver.security.config;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import io.oauth.oauth2authorizationclientserver.security.common.JwtGenerator;
import io.oauth.oauth2authorizationclientserver.security.signer.RSASecuritySigner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.crypto.keygen.Base64StringKeyGenerator;
import org.springframework.security.crypto.keygen.StringKeyGenerator;

import java.util.Base64;
import java.util.UUID;

@Configuration
public class TokenConfig {

    @Bean
    public JwtGenerator jwtGenerator() throws JOSEException {
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
    public RSAKey rsaKey() throws JOSEException {
        String keyId = UUID.randomUUID().toString();
        RSAKey rsaKey = new RSAKeyGenerator(2048)
                .keyID(keyId)
                .algorithm(JWSAlgorithm.RS256)
                .generate();

        return rsaKey;
    }


}
