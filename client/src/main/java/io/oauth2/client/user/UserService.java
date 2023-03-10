package io.oauth2.client.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public User findById(String userId){
        return userRepository.findByUserId(userId);
    }

    @Transactional
    public int addUserRole(String userId, String roleId){
        return userRepository.addUserRole(userId, roleId);
    }

    @Transactional
    public int deleteUserRole(String userId, String roleId){
        return userRepository.deleteUserRole(userId, roleId);
    }

    @Transactional(readOnly = true)
    public List<User> findAll(){
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Map<String, Object> findUsers(Long offset, int size) {
        return userRepository.findUsers(offset, size);
    }
}
