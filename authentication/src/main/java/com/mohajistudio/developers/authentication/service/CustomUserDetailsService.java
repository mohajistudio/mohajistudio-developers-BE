package com.mohajistudio.developers.authentication.service;

import com.mohajistudio.developers.database.entity.User;
import com.mohajistudio.developers.database.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> findUser = userRepository.findByEmail(email);

        if(findUser.isEmpty()) {
            throw new UsernameNotFoundException(email);
        }

        User user = findUser.get();

        if(user.getPassword() != null) {
            return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), Collections.singletonList(new SimpleGrantedAuthority(user.getRole().name())));
        }
        return new org.springframework.security.core.userdetails.User(user.getEmail(), "unregistered", Collections.singletonList(new SimpleGrantedAuthority(user.getRole().name())));
    }
}
