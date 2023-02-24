package io.oauth.resourceserverrolesresources.service;

import io.oauth.resourceserverrolesresources.repository.RoleRepository;
import io.oauth.resourceserverrolesresources.web.domain.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Iterator;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class RoleService {

    private final RoleHierarchyImpl roleHierarchy;
    private final RoleRepository roleRepository;


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


    public int addRole(Role role, String parentId){
        int result = roleRepository.addRole(role, parentId);
        setRoleHierarchy();

        return result;
    }
    public int updateRole(Role role, String parentRoleId){
        int result = roleRepository.updateRole(role, parentRoleId);
        setRoleHierarchy();
        return result;
    }

    public int deleteRole(String roleId){
        int result = roleRepository.deleteRole(roleId);
        setRoleHierarchy();
        return result;
    }

    public int addRoleOfUser(String userId, String roleId){
        return roleRepository.addRoleOfUser(userId, roleId);
    }

    public int updateRoleOfUser(String userId, String roleId){
        return roleRepository.updateRoleOfUser(userId, roleId);
    }

    public List<Role> getRoles(){
        return roleRepository.findAll();
    }
}
