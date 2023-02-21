package io.oauth.oauth2authorizationclientserver.security.init;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.shaded.json.JSONObject;
import com.nimbusds.jose.util.Base64URL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;

@Component
public class SecurityInitializer implements ApplicationRunner {

    @Autowired
    private ResourceLoader resourceLoader;

    @Autowired
    private JWK jwk;

    @Value("${file.jwks.path}")
    private String filePath;

    @Override
    public void run(ApplicationArguments args) throws Exception {
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


        try {
            File file = new File(filePath);
            BufferedWriter writer = new BufferedWriter(new FileWriter(file, false));
            writer.write(jsonObject.toJSONString());

            writer.flush();
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
