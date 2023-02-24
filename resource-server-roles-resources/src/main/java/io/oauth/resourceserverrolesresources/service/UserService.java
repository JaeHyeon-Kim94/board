package io.oauth.resourceserverrolesresources.service;

import io.oauth.resourceserverrolesresources.repository.UserRepository;
import io.oauth.resourceserverrolesresources.web.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    public User findById(String userId){
        return userRepository.findByUserId(userId);
    }
    public List<User> findAll(){
        return userRepository.findAll();
    }
}
