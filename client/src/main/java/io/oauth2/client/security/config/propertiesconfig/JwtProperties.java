package io.oauth2.client.security.config.propertiesconfig;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

import java.util.ArrayList;
import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "custom.jwt")
public class JwtProperties{
    private List<String> trustedIssuerUri = new ArrayList<>();
    int cookieMaxAge;
    private String idTokenCookieName;
    private String regIdCookieName;

    public List<String> getTrustedIssuerUri() {
        return trustedIssuerUri;
    }

    public void setTrustedIssuerUri(List<String> trustedIssuerUri) {
        this.trustedIssuerUri = trustedIssuerUri;
    }

    public int getCookieMaxAge() {
        return cookieMaxAge;
    }

    public void setCookieMaxAge(int cookieMaxAge) {
        this.cookieMaxAge = cookieMaxAge;
    }

    public String getIdTokenCookieName() {
        return idTokenCookieName;
    }

    public void setIdTokenCookieName(String idTokenCookieName) {
        this.idTokenCookieName = idTokenCookieName;
    }

    public String getRegIdCookieName() {
        return regIdCookieName;
    }

    public void setRegIdCookieName(String regIdCookieName) {
        this.regIdCookieName = regIdCookieName;
    }
}
