package io.oauth2.client.security.filter;

import org.springframework.http.HttpMethod;
import org.springframework.security.access.intercept.InterceptorStatusToken;
import org.springframework.security.authentication.AuthenticationManagerResolver;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class PermitAllFilter extends FilterSecurityInterceptor {

    private static final String FILTER_APPLIED = "__spring_security_filterSecurityInterceptor_filterApplied";

    private List<RequestMatcher> permitAllRequestMatcher = new ArrayList<>();

    private final AuthenticationManagerResolver<HttpServletRequest> authenticationManagerResolver;

    public PermitAllFilter(AuthenticationManagerResolver<HttpServletRequest> authenticationManagerResolver, String... permitAllPattern) {
        createPermitAllPattern(permitAllPattern);
        this.authenticationManagerResolver = authenticationManagerResolver;
    }

    private void createPermitAllPattern(String[] permitAllPattern) {
        for(String pattern : permitAllPattern){
            permitAllRequestMatcher.add(new AntPathRequestMatcher(pattern, HttpMethod.GET.name()));
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        invoke(new FilterInvocation(request, response, chain));
    }

    @Override
    public Class<?> getSecureObjectClass() {
        return FilterInvocation.class;
    }

    @Override
    protected InterceptorStatusToken beforeInvocation(Object object) {

        boolean isPermitAll = false;

        HttpServletRequest request = ((FilterInvocation) object).getRequest();
        for(RequestMatcher requestMatcher : permitAllRequestMatcher){
            if(requestMatcher.matches(request)){
                isPermitAll = true;
                break;
            }
        }
        if (isPermitAll){
            return null;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication!=null && !authentication.isAuthenticated()) {
            super.setAuthenticationManager(authenticationManagerResolver.resolve(request));
        }

        return super.beforeInvocation(object);
    }

    public void invoke(FilterInvocation filterInvocation) throws IOException, ServletException {
        if (isApplied(filterInvocation) && super.isObserveOncePerRequest()) {
            filterInvocation.getChain().doFilter(filterInvocation.getRequest(), filterInvocation.getResponse());
            return;
        } else {
            filterInvocation.getRequest().setAttribute(FILTER_APPLIED, Boolean.TRUE);
        }
        InterceptorStatusToken token = beforeInvocation(filterInvocation);
        try {
            filterInvocation.getChain().doFilter(filterInvocation.getRequest(), filterInvocation.getResponse());
        }
        finally {
            super.finallyInvocation(token);
        }
        super.afterInvocation(token, null);
    }

    private boolean isApplied(FilterInvocation filterInvocation) {
        return (filterInvocation.getRequest() != null)
                && (filterInvocation.getRequest().getAttribute(FILTER_APPLIED) != null);
    }
}
