package io.oauth2.client.security.config.propertiesconfig;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "custom.jwt")
public class JwtProperties{
    private List<String> trustedIssuerUri = new ArrayList<>();

    public List<String> getTrustedIssuerUri() {
        return trustedIssuerUri;
    }

    public void setTrustedIssuerUri(List<String> trustedIssuerUri) {
        this.trustedIssuerUri = trustedIssuerUri;
    }

    int cookieMaxAge;

    public int getCookieMaxAge() {
        return cookieMaxAge;
    }

    public void setCookieMaxAge(int cookieMaxAge) {
        this.cookieMaxAge = cookieMaxAge;
    }
}