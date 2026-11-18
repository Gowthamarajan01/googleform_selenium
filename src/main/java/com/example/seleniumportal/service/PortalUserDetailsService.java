package com.example.seleniumportal.service;

import com.example.seleniumportal.model.PortalUser;
import com.example.seleniumportal.repository.PortalUserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class PortalUserDetailsService implements UserDetailsService {

    private final PortalUserRepository userRepository;

    public PortalUserDetailsService(PortalUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        PortalUser portalUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return User.withUsername(portalUser.getUsername())
                .password(portalUser.getPassword())
                .roles("USER")
                .build();
    }
}
