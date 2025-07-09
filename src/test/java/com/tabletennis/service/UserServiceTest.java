package com.tabletennis.service;

import java.util.Optional;

import com.tabletennis.TestDataFactory;
import com.tabletennis.entity.User;
import com.tabletennis.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

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
        var username = user.getUsername();
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        // When
        var result = userService.loadUserByUsername(username);

        // Then
        assertNotNull(result);
        assertEquals(username, result.getUsername());
        assertEquals(user.getPassword(), result.getPassword());
        assertTrue(result.isEnabled());
        assertTrue(result.isAccountNonExpired());
        assertTrue(result.isAccountNonLocked());
        assertTrue(result.isCredentialsNonExpired());
        verify(userRepository).findByUsername(username);
    }

    @Test
    void loadUserByUsername_WhenUserDoesNotExist_ShouldThrowException() {
        // Given
        var username = "nonexistent";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername(username));
        verify(userRepository).findByUsername(username);
    }

    @Test
    void findByUsername_WhenUserExists_ShouldReturnUser() {
        // Given
        var username = user.getUsername();
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        // When
        var result = userService.findByUsername(username);

        // Then
        assertTrue(result.isPresent());
        assertEquals(user, result.get());
        verify(userRepository).findByUsername(username);
    }

    @Test
    void needsEmailSetup_WhenUserHasNoEmail_ShouldReturnTrue() {
        // Given
        var username = user.getUsername();
        user.setEmail(null);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        // When
        var result = userService.needsEmailSetup(username);

        // Then
        assertTrue(result);
        verify(userRepository).findByUsername(username);
    }

    @Test
    void needsEmailSetup_WhenUserHasEmptyEmail_ShouldReturnTrue() {
        // Given
        var username = user.getUsername();
        user.setEmail("");
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        // When
        var result = userService.needsEmailSetup(username);

        // Then
        assertTrue(result);
        verify(userRepository).findByUsername(username);
    }

    @Test
    void needsEmailSetup_WhenUserHasEmail_ShouldReturnFalse() {
        // Given
        var username = user.getUsername();
        user.setEmail("test@example.com");
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        // When
        var result = userService.needsEmailSetup(username);

        // Then
        assertFalse(result);
        verify(userRepository).findByUsername(username);
    }

    @Test
    void needsEmailSetup_WhenUserDoesNotExist_ShouldReturnFalse() {
        // Given
        var username = "nonexistent";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // When
        var result = userService.needsEmailSetup(username);

        // Then
        assertFalse(result);
        verify(userRepository).findByUsername(username);
    }

    @Test
    void updateUserEmail_WhenUserExists_ShouldUpdateEmail() {
        // Given
        var username = user.getUsername();
        var newEmail = "newemail@example.com";
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
    void updateUserEmail_WhenUserDoesNotExist_ShouldThrowException() {
        // Given
        var username = "nonexistent";
        var newEmail = "newemail@example.com";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> userService.updateUserEmail(username, newEmail));
        verify(userRepository).findByUsername(username);
    }

    @Test
    void isEmailTaken_WhenEmailTakenByOtherUser_ShouldReturnTrue() {
        // Given
        var email = "test@example.com";
        var currentUsername = "currentuser";
        var otherUser = TestDataFactory.createUser();
        otherUser.setUsername("otheruser");
        otherUser.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(otherUser));

        // When
        var result = userService.isEmailTaken(email, currentUsername);

        // Then
        assertTrue(result);
        verify(userRepository).findByEmail(email);
    }

    @Test
    void isEmailTaken_WhenEmailTakenBySameUser_ShouldReturnFalse() {
        // Given
        var email = "test@example.com";
        var currentUsername = "currentuser";
        var sameUser = TestDataFactory.createUser();
        sameUser.setUsername(currentUsername);
        sameUser.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(sameUser));

        // When
        var result = userService.isEmailTaken(email, currentUsername);

        // Then
        assertFalse(result);
        verify(userRepository).findByEmail(email);
    }

    @Test
    void isEmailTaken_WhenEmailNotTaken_ShouldReturnFalse() {
        // Given
        var email = "test@example.com";
        var currentUsername = "currentuser";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // When
        var result = userService.isEmailTaken(email, currentUsername);

        // Then
        assertFalse(result);
        verify(userRepository).findByEmail(email);
    }
}
