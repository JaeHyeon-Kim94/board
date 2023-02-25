package io.oauth.resourceserverrolesresources.service;

import io.oauth.resourceserverrolesresources.repository.UserRepository;
import io.oauth.resourceserverrolesresources.web.domain.User;
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

    @Transactional(readOnly = true)
    public List<User> findAll(){
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Map<String, Object> findUsers(int offset, int size) {
        return userRepository.findUsers(offset, size);
    }
}
