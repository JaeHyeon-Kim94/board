package io.oauth.authorizationserver.service;

import io.oauth.authorizationserver.repository.UserRepository;
import io.oauth.authorizationserver.web.domain.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public User save(User user){

        if(userRepository.findByUserId(user.getUserId()) == null){
            return userRepository.insert(user);
        }

        userRepository.update(user);
        return user;
    }

    public boolean checkIsDuplicated(String type, String value) {
        return userRepository.isDuplicate(type, value);
    }
}
