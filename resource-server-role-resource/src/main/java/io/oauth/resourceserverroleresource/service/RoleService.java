package io.oauth.resourceserverroleresource.service;

import io.oauth.resourceserverroleresource.repository.RoleRepository;
import io.oauth.resourceserverroleresource.web.domain.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Iterator;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class RoleService {

    private final RoleHierarchyImpl roleHierarchy;
    private final RoleRepository roleRepository;

    @Transactional
    public String findRoleHierarchy(){
        List<Role> roles = roleRepository.findAll();
        Iterator<Role> iterator = roles.iterator();

        StringBuilder concatedRoles = new StringBuilder();
        while(iterator.hasNext()){
            Role role = iterator.next();
            if(role.getParent() != null){
                concatedRoles.append(role.getParent().getName());
                concatedRoles.append(" > ");
                concatedRoles.append(role.getName());
                concatedRoles.append("\n");
            }
        }
        log.info("\nconcatedRoles.toString : \n{}", concatedRoles.toString());
        return concatedRoles.toString();
    }

    public void setRoleHierarchy(){
        String formattedRoleHierarchy = findRoleHierarchy();
        roleHierarchy.setHierarchy(formattedRoleHierarchy);
    }

}
