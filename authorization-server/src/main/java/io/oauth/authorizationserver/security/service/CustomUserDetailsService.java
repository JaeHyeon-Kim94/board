package io.oauth.authorizationserver.security.service;

import io.oauth.authorizationserver.repository.UserRepository;
import io.oauth.authorizationserver.security.model.Principal;
import io.oauth.authorizationserver.web.domain.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByUsername(username);

        if(user == null)
            throw new UsernameNotFoundException("No user with username : " + username);

        return new Principal(user);
    }
}
