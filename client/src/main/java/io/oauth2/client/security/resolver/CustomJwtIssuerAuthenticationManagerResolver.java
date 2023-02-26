package io.oauth2.client.security.resolver;

import com.nimbusds.jwt.JWTParser;
import io.oauth2.client.security.converter.CustomJwtAuthenticationConverter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.log.LogMessage;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationManagerResolver;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.server.resource.BearerTokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

public class CustomJwtIssuerAuthenticationManagerResolver implements AuthenticationManagerResolver<HttpServletRequest> {

    private AuthenticationProvider authenticationProvider;

    private final AuthenticationManager authenticationManager;

    public CustomJwtIssuerAuthenticationManagerResolver(String... trustedIssuers) {
        this(Arrays.asList(trustedIssuers));
    }

    public CustomJwtIssuerAuthenticationManagerResolver(Collection<String> trustedIssuers){
        Assert.notEmpty(trustedIssuers, "trustedIssuers cannot be empty");
        this.authenticationManager = new ResolvingAuthenticationManager(
                new TrustedIssuerJwtAuthenticationManagerResolver(
                        Collections.unmodifiableCollection(trustedIssuers)::contains));
    }

    @Override
    public AuthenticationManager resolve(HttpServletRequest context) {
        return this.authenticationManager;
    }

    private static class ResolvingAuthenticationManager implements AuthenticationManager{

        private final Converter<BearerTokenAuthenticationToken, String> issuerConverter = new JwtClaimIssuerConverter();

        private final AuthenticationManagerResolver<String> issuerAuthenticationManagerResolver;

        ResolvingAuthenticationManager(AuthenticationManagerResolver<String> issuerAuthenticationManagerResolver) {
            this.issuerAuthenticationManagerResolver = issuerAuthenticationManagerResolver;
        }
        @Override
        public Authentication authenticate(Authentication authentication) throws AuthenticationException {
            Assert.isTrue(authentication instanceof BearerTokenAuthenticationToken,
                    "Authentication must be of type BearerTokenAuthenticationToken");
            BearerTokenAuthenticationToken token
                    = (BearerTokenAuthenticationToken) authentication;
            String issuer = this.issuerConverter.convert(token);
            AuthenticationManager authenticationManager
                    = this.issuerAuthenticationManagerResolver.resolve(issuer);
            if(authenticationManager == null){
                throw new InvalidBearerTokenException("Invalid issuer");
            }
            return authenticationManager.authenticate(authentication);
        }
    }

    private static class JwtClaimIssuerConverter implements Converter<BearerTokenAuthenticationToken, String> {

        @Override
        public String convert(@NonNull BearerTokenAuthenticationToken authentication) {
            String token = authentication.getToken();
            try {
                String issuer = JWTParser.parse(token).getJWTClaimsSet().getIssuer();
                if (issuer != null) {
                    return issuer;
                }
            }
            catch (Exception ex) {
                throw new InvalidBearerTokenException(ex.getMessage(), ex);
            }
            throw new InvalidBearerTokenException("Missing issuer");
        }

    }

    static class TrustedIssuerJwtAuthenticationManagerResolver implements AuthenticationManagerResolver<String> {

        private final Log logger = LogFactory.getLog(getClass());

        private final Map<String, AuthenticationManager> authenticationManagers = new ConcurrentHashMap<>();

        private final Predicate<String> trustedIssuer;

        TrustedIssuerJwtAuthenticationManagerResolver(Predicate<String> trustedIssuer) {
            this.trustedIssuer = trustedIssuer;
        }

        @Override
        public AuthenticationManager resolve(String issuer) {
            if (this.trustedIssuer.test(issuer)) {
                AuthenticationManager authenticationManager = this.authenticationManagers.computeIfAbsent(issuer,
                        (k) -> {
                            this.logger.debug("Constructing AuthenticationManager");
                            JwtDecoder jwtDecoder = JwtDecoders.fromIssuerLocation(issuer);
                            JwtAuthenticationProvider jwtAuthenticationProvider = new JwtAuthenticationProvider(jwtDecoder);
                            jwtAuthenticationProvider.setJwtAuthenticationConverter(new CustomJwtAuthenticationConverter());
                            return authentication -> jwtAuthenticationProvider.authenticate(authentication);
                        });
                this.logger.debug(LogMessage.format("Resolved AuthenticationManager for issuer '%s'", issuer));
                return authenticationManager;
            }
            else {
                this.logger.debug("Did not resolve AuthenticationManager since issuer is not trusted");
            }
            return null;
        }

    }
}
