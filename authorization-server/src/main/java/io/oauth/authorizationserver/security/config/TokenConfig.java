package io.oauth.authorizationserver.security.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import io.oauth.authorizationserver.security.customizer.JwtGeneratorCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.authorization.token.*;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

@Configuration
public class TokenConfig {

    @Bean
    public OAuth2TokenGenerator<? extends OAuth2Token> tokenGenerator(){
        //JwtGenerator
        JwtGenerator jwtGenerator = jwtGenerator();

        jwtGenerator.setJwtCustomizer(jwtGeneratorCustomizer());

        //AccessToken Generator
        OAuth2AccessTokenGenerator oAuth2AccessTokenGenerator = oAuth2AccessTokenGenerator();

//        oAuth2AccessTokenGenerator.setAccessTokenCustomizer(context -> {
//
//        });

        //RefreshToken Generator
        OAuth2RefreshTokenGenerator oAuth2RefreshTokenGenerator = oAuth2RefreshTokenGenerator();

        return new DelegatingOAuth2TokenGenerator(jwtGenerator, oAuth2AccessTokenGenerator, oAuth2RefreshTokenGenerator);
    }

    @Bean
    public JwtGenerator jwtGenerator(){
        return new JwtGenerator(jwtEncoder());
    }


    @Bean
    public JwtGeneratorCustomizer jwtGeneratorCustomizer(){
        return new JwtGeneratorCustomizer();
    }

    @Bean
    public OAuth2AccessTokenGenerator oAuth2AccessTokenGenerator(){
        return new OAuth2AccessTokenGenerator();
    }

    @Bean
    public OAuth2RefreshTokenGenerator oAuth2RefreshTokenGenerator(){
        return new OAuth2RefreshTokenGenerator();
    }

    @Bean
    public NimbusJwtEncoder jwtEncoder(){
        return new NimbusJwtEncoder(jwkSource());
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        RSAKey rsaKey = generateRsaKey();

        JWKSet jwkSet = new JWKSet(rsaKey);

        return (jwkSelector, context) -> jwkSelector.select(jwkSet);
    }

    private RSAKey generateRsaKey() {
        //KeyPairGenerator
        KeyPairGenerator keyGen;
        try {
            keyGen = KeyPairGenerator.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("KeyPairGenerator getInstance error ", e.getCause());
        }
        keyGen.initialize(2048);

        //KeyPair from generator
        KeyPair keyPair = keyGen.generateKeyPair();

        //KeyPair to RSAKey
        RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) keyPair.getPrivate();
        RSAPublicKey rsaPublicKey = (RSAPublicKey) keyPair.getPublic();
        RSAKey rsaKey = new RSAKey.Builder(rsaPublicKey)
                .privateKey(rsaPrivateKey)
                .keyID(UUID.randomUUID().toString())
                .build();
        return rsaKey;
    }

}
