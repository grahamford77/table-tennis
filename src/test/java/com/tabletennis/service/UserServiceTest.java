package com.tabletennis.service;

import com.tabletennis.TestDataFactory;
import com.tabletennis.entity.User;
import com.tabletennis.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    private UserService userService;
    private User user;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository);

        // Create test data using TestDataFactory
        user = TestDataFactory.createUser();
    }

    @Test
    void loadUserByUsername_WhenUserExists_ShouldReturnUserDetails() {
        // Given
        String username = user.getUsername();
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        // When
        UserDetails result = userService.loadUserByUsername(username);

        // Then
        assertNotNull(result);
        assertEquals(username, result.getUsername());
        assertEquals(user.getPassword(), result.getPassword());
        assertTrue(result.isAccountNonExpired());
        assertTrue(result.isAccountNonLocked());
        assertTrue(result.isCredentialsNonExpired());
        assertTrue(result.isEnabled());
        verify(userRepository).findByUsername(username);
    }

    @Test
    void loadUserByUsername_WhenUserDoesNotExist_ShouldThrowUsernameNotFoundException() {
        // Given
        String username = "nonexistent";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername(username));
        verify(userRepository).findByUsername(username);
    }

    @Test
    void findByUsername_WhenUserExists_ShouldReturnUser() {
        // Given
        String username = user.getUsername();
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        // When
        Optional<User> result = userService.findByUsername(username);

        // Then
        assertTrue(result.isPresent());
        assertEquals(user, result.get());
        verify(userRepository).findByUsername(username);
    }

    @Test
    void needsEmailSetup_WhenUserHasNoEmail_ShouldReturnTrue() {
        // Given
        String username = user.getUsername();
        user.setEmail(null);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        // When
        boolean result = userService.needsEmailSetup(username);

        // Then
        assertTrue(result);
        verify(userRepository).findByUsername(username);
    }

    @Test
    void needsEmailSetup_WhenUserHasEmptyEmail_ShouldReturnTrue() {
        // Given
        String username = user.getUsername();
        user.setEmail("   ");
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        // When
        boolean result = userService.needsEmailSetup(username);

        // Then
        assertTrue(result);
        verify(userRepository).findByUsername(username);
    }

    @Test
    void needsEmailSetup_WhenUserHasEmail_ShouldReturnFalse() {
        // Given
        String username = user.getUsername();
        user.setEmail("test@example.com");
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        // When
        boolean result = userService.needsEmailSetup(username);

        // Then
        assertFalse(result);
        verify(userRepository).findByUsername(username);
    }

    @Test
    void needsEmailSetup_WhenUserDoesNotExist_ShouldReturnFalse() {
        // Given
        String username = "nonexistent";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // When
        boolean result = userService.needsEmailSetup(username);

        // Then
        assertFalse(result);
        verify(userRepository).findByUsername(username);
    }

    @Test
    void updateUserEmail_WhenUserExists_ShouldUpdateEmail() {
        // Given
        String username = user.getUsername();
        String newEmail = "newemail@example.com";
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        // When
        userService.updateUserEmail(username, newEmail);

        // Then
        assertEquals(newEmail, user.getEmail());
        verify(userRepository).findByUsername(username);
        verify(userRepository).save(user);
    }

    @Test
    void updateUserEmail_WhenUserDoesNotExist_ShouldThrowUsernameNotFoundException() {
        // Given
        String username = "nonexistent";
        String newEmail = "newemail@example.com";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(UsernameNotFoundException.class, () -> userService.updateUserEmail(username, newEmail));
        verify(userRepository).findByUsername(username);
    }

    @Test
    void isEmailTaken_WhenEmailExistsForDifferentUser_ShouldReturnTrue() {
        // Given
        String email = "test@example.com";
        String currentUsername = "currentuser";
        User otherUser = TestDataFactory.createUser();
        otherUser.setUsername("otheruser");
        otherUser.setEmail(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(otherUser));

        // When
        boolean result = userService.isEmailTaken(email, currentUsername);

        // Then
        assertTrue(result);
        verify(userRepository).findByEmail(email);
    }

    @Test
    void isEmailTaken_WhenEmailExistsForSameUser_ShouldReturnFalse() {
        // Given
        String email = "test@example.com";
        String currentUsername = "currentuser";
        User sameUser = TestDataFactory.createUser();
        sameUser.setUsername(currentUsername);
        sameUser.setEmail(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(sameUser));

        // When
        boolean result = userService.isEmailTaken(email, currentUsername);

        // Then
        assertFalse(result);
        verify(userRepository).findByEmail(email);
    }

    @Test
    void isEmailTaken_WhenEmailDoesNotExist_ShouldReturnFalse() {
        // Given
        String email = "test@example.com";
        String currentUsername = "currentuser";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // When
        boolean result = userService.isEmailTaken(email, currentUsername);

        // Then
        assertFalse(result);
        verify(userRepository).findByEmail(email);
    }
}
