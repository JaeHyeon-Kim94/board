package io.oauth2.client.role;

import io.oauth2.client.role.dto.RoleRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
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

    @Transactional(readOnly = true, propagation = Propagation.NESTED)
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

    /**
     * @param dto
     * @return put 요청에 대해 insert or update 처리 여부 반환(insert시 true, update시 false)
     */
    @Transactional
    public boolean putRole(RoleRequestDto dto){

        if(!dto.getName().contains("ROLE_")){
            dto.setName("ROLE_"+dto.getName());
        }

        Role role = roleRepository.findById(dto.getId());
        if(role == null){
            roleRepository.addRole(RoleRequestDto.toRole(dto), dto.getParentId());
            return true;
        }

        roleRepository.updateRole(RoleRequestDto.toRole(dto), dto.getParentId());

        //계층구조 재설정.
        setRoleHierarchy();
        return false;
    }

    @Transactional
    public int deleteRole(String roleId){
        int result = roleRepository.deleteRole(roleId);
        //계층구조 재설정.
        setRoleHierarchy();
        return result;
    }

    @Transactional
    public Role findByid(String roleId){
        return roleRepository.findById(roleId);
    }

    @Transactional(readOnly = true)
    public List<Role> findAll(){
        return roleRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Map<String, Object> findRoles(Long offset, int size){
        return roleRepository.findRoles(offset, size);
    }
}
