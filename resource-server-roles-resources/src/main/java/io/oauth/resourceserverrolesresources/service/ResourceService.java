package io.oauth.resourceserverrolesresources.service;

import io.oauth.resourceserverrolesresources.repository.ResourceRepository;
import io.oauth.resourceserverrolesresources.web.domain.Resource;
import io.oauth.resourceserverrolesresources.web.domain.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@RequiredArgsConstructor
@Transactional
@Service
public class ResourceService {

    private final ResourceRepository resourceRepository;

    public LinkedHashMap<RequestMatcher, List<ConfigAttribute>> getResourceList(){
        LinkedHashMap<RequestMatcher, List<ConfigAttribute>> resources = new LinkedHashMap<>();
        List<Resource> foundResources = resourceRepository.findAll();

        foundResources.forEach(resource-> {
            Role role = resource.getRole();
            SecurityConfig sc = new SecurityConfig(role.getName());
            resources.put(new AntPathRequestMatcher(resource.getValue(), resource.getHttpMethod()), List.of(sc));
        });

        return resources;
    }

    public Resource addResource(Resource resource, String roleId){
        return resourceRepository.addRole(resource, roleId);
    }

    public int updateResource(Resource resource, String roleId) {
        return resourceRepository.updateRole(resource, roleId);
    }

    public int deleteResource(String resourceId) {
        return resourceRepository.deleteResource(resourceId);
    }

    public List<Resource> findAll(){
        return resourceRepository.findAll();
    }

    public Resource findById(String resourceId){
        return resourceRepository.findById(resourceId);
    }
}
