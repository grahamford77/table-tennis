package com.tabletennis.repository;

import com.tabletennis.TestDataFactory;
import com.tabletennis.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class UserRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByUsername_WhenUserExists_ShouldReturnUser() {
        // Given
        User user = TestDataFactory.createUser();
        String username = user.getUsername();
        entityManager.persistAndFlush(user);

        // When
        Optional<User> result = userRepository.findByUsername(username);

        // Then
        assertTrue(result.isPresent());
        assertEquals(user.getId(), result.get().getId());
        assertEquals(username, result.get().getUsername());
        assertEquals(user.getPassword(), result.get().getPassword());
    }

    @Test
    void findByUsername_WhenUserDoesNotExist_ShouldReturnEmpty() {
        // Given
        String nonExistentUsername = "nonexistent";

        // When
        Optional<User> result = userRepository.findByUsername(nonExistentUsername);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void findByUsername_WhenMultipleUsersExist_ShouldReturnCorrectUser() {
        // Given
        User user1 = TestDataFactory.createUser();
        User user2 = TestDataFactory.createUser();
        entityManager.persistAndFlush(user1);
        entityManager.persistAndFlush(user2);

        // When
        Optional<User> result = userRepository.findByUsername(user1.getUsername());

        // Then
        assertTrue(result.isPresent());
        assertEquals(user1.getId(), result.get().getId());
        assertEquals(user1.getUsername(), result.get().getUsername());
    }

    @Test
    void existsByUsername_WhenUserExists_ShouldReturnTrue() {
        // Given
        User user = TestDataFactory.createUser();
        String username = user.getUsername();
        entityManager.persistAndFlush(user);

        // When
        boolean result = userRepository.existsByUsername(username);

        // Then
        assertTrue(result);
    }

    @Test
    void existsByUsername_WhenUserDoesNotExist_ShouldReturnFalse() {
        // Given
        String nonExistentUsername = "nonexistent";

        // When
        boolean result = userRepository.existsByUsername(nonExistentUsername);

        // Then
        assertFalse(result);
    }

    @Test
    void existsByUsername_WithCaseVariation_ShouldBeExact() {
        // Given
        User user = TestDataFactory.createUser();
        user.setUsername("testuser");
        entityManager.persistAndFlush(user);

        // When
        boolean resultUpperCase = userRepository.existsByUsername("TESTUSER");
        boolean resultLowerCase = userRepository.existsByUsername("testuser");

        // Then
        assertFalse(resultUpperCase); // Username search should be exact
        assertTrue(resultLowerCase);
    }
}
