package com.tabletennis.service;

import com.tabletennis.entity.User;
import com.tabletennis.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service class for user management and authentication
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * Load user by username for Spring Security
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities("ROLE_" + user.getRole().name())
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(!user.isEnabled())
                .build();
    }

    /**
     * Find user by username
     */
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Check if user needs to set up email
     */
    public boolean needsEmailSetup(String username) {
        var user = userRepository.findByUsername(username);
        return user.isPresent() && (user.get().getEmail() == null || user.get().getEmail().trim().isEmpty());
    }

    /**
     * Update user's email address
     */
    @Transactional
    public void updateUserEmail(String username, String email) {
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        user.setEmail(email);
        userRepository.save(user);

        log.info("Updated email for user: {}", username);
    }

    /**
     * Check if email is already taken by another user
     */
    public boolean isEmailTaken(String email, String currentUsername) {
        var userWithEmail = userRepository.findByEmail(email);
        return userWithEmail.isPresent() && !userWithEmail.get().getUsername().equals(currentUsername);
    }
}
