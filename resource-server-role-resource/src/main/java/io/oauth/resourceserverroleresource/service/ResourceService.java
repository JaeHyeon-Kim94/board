package io.oauth.resourceserverroleresource.service;

import io.oauth.resourceserverroleresource.repository.ResourceRepository;
import io.oauth.resourceserverroleresource.web.domain.Resource;
import io.oauth.resourceserverroleresource.web.domain.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class ResourceService {

    private final ResourceRepository resourceRepository;

    @Transactional
    public LinkedHashMap<RequestMatcher, List<ConfigAttribute>> getResourceList(){
        LinkedHashMap<RequestMatcher, List<ConfigAttribute>> resources = new LinkedHashMap<>();
        List<Resource> foundResources = resourceRepository.findAll();

        foundResources.forEach(resource-> {
            List<ConfigAttribute> configAttributes = new ArrayList<>();
            Set<Role> roles = resource.getRoles();
            roles.iterator().forEachRemaining(role -> configAttributes.add(new SecurityConfig(role.getName())));


            resources.put(new AntPathRequestMatcher(resource.getValue()), configAttributes);
        });

        return resources;
    }
}
