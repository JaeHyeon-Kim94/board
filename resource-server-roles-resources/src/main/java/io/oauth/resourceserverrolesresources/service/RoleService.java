package io.oauth.resourceserverrolesresources.service;

import io.oauth.resourceserverrolesresources.repository.RoleRepository;
import io.oauth.resourceserverrolesresources.web.domain.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class RoleService {

    private final RoleHierarchyImpl roleHierarchy;
    private final RoleRepository roleRepository;

    @Transactional(readOnly = true)
    public void setRoleHierarchy(){
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

        roleHierarchy.setHierarchy(concatedRoles.toString());
    }

    @Transactional
    public int addRole(Role role, String parentId){
        int result = roleRepository.addRole(role, parentId);
        setRoleHierarchy();

        return result;
    }

    @Transactional
    public int updateRole(Role role, String parentRoleId){
        int result = roleRepository.updateRole(role, parentRoleId);
        setRoleHierarchy();
        return result;
    }

    @Transactional
    public int deleteRole(String roleId){
        int result = roleRepository.deleteRole(roleId);
        setRoleHierarchy();
        return result;
    }

    @Transactional
    public int addRoleOfUser(String userId, String roleId){
        return roleRepository.addRoleOfUser(userId, roleId);
    }

    @Transactional
    public int updateRoleOfUser(String userId, String roleId){
        return roleRepository.updateRoleOfUser(userId, roleId);
    }

    @Transactional(readOnly = true)
    public List<Role> findAll(){
        return roleRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Map<String, Object> findRoles(int offset, int size){
        return roleRepository.findRoles(offset, size);
    }
}
