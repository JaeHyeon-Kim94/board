package io.oauth.oauth2authorizationclientserver.security.signer;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.RSAKey;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class RSASecuritySigner extends SecuritySigner {

    public String getJwtToken(OAuth2User user, JWK jwk, String tokenType) throws JOSEException {

        RSASSASigner jwsSigner = new RSASSASigner(((RSAKey)jwk).toRSAPrivateKey());
        return getJwtTokenInternal(jwsSigner, user, jwk, tokenType);

    }
}
