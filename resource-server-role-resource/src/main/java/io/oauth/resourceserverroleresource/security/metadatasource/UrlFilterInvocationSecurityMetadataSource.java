package io.oauth.resourceserverroleresource.security.metadatasource;

import io.oauth.resourceserverroleresource.service.ResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class UrlFilterInvocationSecurityMetadataSource implements FilterInvocationSecurityMetadataSource {

    private LinkedHashMap<RequestMatcher, List<ConfigAttribute>> resourceAuthorityMap;
    private final ResourceService resourceService;

    public UrlFilterInvocationSecurityMetadataSource(LinkedHashMap<RequestMatcher, List<ConfigAttribute>> resourceAuthorityMap, ResourceService resourceService) {
        this.resourceAuthorityMap = resourceAuthorityMap;
        this.resourceService = resourceService;
    }

    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {

        HttpServletRequest request = ((FilterInvocation) object).getRequest();

        if(resourceAuthorityMap != null){
            for (Map.Entry<RequestMatcher, List<ConfigAttribute>> entry : resourceAuthorityMap.entrySet()) {
                RequestMatcher requestMatcher = entry.getKey();
                if(requestMatcher.matches(request)){
                    return entry.getValue();
                }
            }
        }
        return null;
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {

        Set<ConfigAttribute> allAttributes = new HashSet<>();
        this.resourceAuthorityMap.values().forEach(allAttributes::addAll);
        return allAttributes;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return FilterInvocation.class.isAssignableFrom(clazz);
    }

    public void reload(){
        LinkedHashMap<RequestMatcher, List<ConfigAttribute>> reloadedMap = resourceService.getResourceList();
        resourceAuthorityMap.clear();
        resourceAuthorityMap.putAll(reloadedMap);
    }
}
