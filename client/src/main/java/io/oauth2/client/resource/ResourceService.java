package io.oauth2.client.resource;

import io.oauth2.client.role.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class ResourceService {

    private final ResourceRepository resourceRepository;

    @Transactional(readOnly = true)
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

    @Transactional
    public Resource addResource(Resource resource, String roleId){
        return resourceRepository.addRole(resource, roleId);
    }

    @Transactional
    public int updateResource(Resource resource, String roleId) {
        return resourceRepository.updateRole(resource, roleId);
    }

    @Transactional
    public int deleteResource(String resourceId) {
        return resourceRepository.deleteResource(resourceId);
    }

    @Transactional(readOnly = true)
    public List<Resource> findAll(){
        return resourceRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Map<String, Object> findResources(Long offset, int size) {
        return resourceRepository.findByResources(offset, size);
    }

    @Transactional(readOnly = true)
    public Resource findById(String resourceId){
        return resourceRepository.findById(resourceId);
    }


}
