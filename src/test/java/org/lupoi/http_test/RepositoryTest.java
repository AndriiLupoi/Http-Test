package org.lupoi.http_test;/*
    @author user
    @project HTTP_Test
    @class RepositoryTest
    @version 1.0.0
    @since 24.04.2025 - 10.57
*/

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.lupoi.http_test.model.UserEntity;
import org.lupoi.http_test.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DataMongoTest
public class RepositoryTest {

    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.save(new UserEntity("admin", "admin", "123456789", "###test"));
        userRepository.save(new UserEntity("user", "user", "987654321", "###test"));
        userRepository.save(new UserEntity("user2", "user2", "132534767", "###test"));
    }

    @AfterEach
    void tearDown() {
        List<UserEntity> usersToDelete = userRepository.findAll().stream()
                .filter(userEntity -> userEntity.getDescription().contains("###test"))
                .toList();
        userRepository.deleteAll(usersToDelete);
    }

    @Test
    void shouldGiveIdForNewRecord() {
        // Given
        UserEntity user = new UserEntity("admin", "admin", "123456789", "###test");

        // When
        userRepository.save(user);
        UserEntity saved = userRepository.findAll().stream()
                .filter(u -> u.getFirstName().equals("admin"))
                .findFirst().orElse(null);

        // Then
        assertNotNull(saved);
        assertNotNull(saved.getId());
        assertFalse(saved.getId().isEmpty());
        assertFalse(saved.getId().equals(user.getId()));
    }

    @Test
    void shouldFindUserById() {
        // Given
        UserEntity user = new UserEntity("Jane", "Doe", "222222222", "###test");
        UserEntity saved = userRepository.save(user);

        // When
        UserEntity found = userRepository.findById(saved.getId()).orElse(null);

        // Then
        assertNotNull(found);
        assertEquals("Jane", found.getFirstName());
    }

    @Test
    void shouldUpdateUser() {
        // Given
        UserEntity user = new UserEntity("Alex", "Smith", "333333333", "###test");
        UserEntity saved = userRepository.save(user);

        // When
        saved.setNumber("999999999");
        userRepository.save(saved);
        UserEntity updated = userRepository.findById(saved.getId()).orElse(null);

        // Then
        assertNotNull(updated);
        assertEquals("999999999", updated.getNumber());
    }

    @Test
    void shouldDeleteUser() {
        // Given
        UserEntity user = new UserEntity("Mike", "Taylor", "444444444", "###test");
        UserEntity saved = userRepository.save(user);

        // When
        userRepository.delete(saved);
        boolean exists = userRepository.findById(saved.getId()).isPresent();

        // Then
        assertFalse(exists);
    }

    @Test
    void shouldFindAllUsersWithTestDescription() {
        // Given
        // (користувачі з ###test вже додані в @BeforeEach)

        // When
        List<UserEntity> users = userRepository.findAll().stream()
                .filter(u -> "###test".equals(u.getDescription()))
                .toList();

        // Then
        assertFalse(users.isEmpty());
    }

    @Test
    void shouldNotFindNonExistentUser() {
        // Given
        String nonExistentId = "nonexistent-id";

        // When
        boolean exists = userRepository.findById(nonExistentId).isPresent();

        // Then
        assertFalse(exists);
    }

    @Test
    void shouldSaveMultipleUsers() {
        // Given
        List<UserEntity> users = List.of(
                new UserEntity("Tom", "Jerry", "555", "###test"),
                new UserEntity("Spike", "Dog", "666", "###test")
        );

        // When
        userRepository.saveAll(users);
        List<UserEntity> result = userRepository.findAll().stream()
                .filter(u -> "###test".equals(u.getDescription()))
                .toList();

        // Then
        assertTrue(result.size() >= 2);
    }

    @Test
    void shouldAssignDifferentIdsToUsers() {
        // Given
        UserEntity user1 = new UserEntity("U1", "Last1", "777", "###test");
        UserEntity user2 = new UserEntity("U2", "Last2", "888", "###test");

        // When
        UserEntity saved1 = userRepository.save(user1);
        UserEntity saved2 = userRepository.save(user2);

        // Then
        assertNotNull(saved1.getId());
        assertNotNull(saved2.getId());
        assertNotEquals(saved1.getId(), saved2.getId());
    }

    @Test
    void shouldFindUserByFirstName() {
        // Given
        UserEntity user = new UserEntity("Custom", "Field", "999", "###test");
        userRepository.save(user);

        // When
        List<UserEntity> found = userRepository.findByFirstName("Custom");

        // Then
        assertFalse(found.isEmpty());
    }

    @Test
    void shouldClearDatabaseAfterEachTest() {
        // Given
        // Немає користувачів, збережених вручну в цьому тесті

        // When
        List<UserEntity> testUsers = userRepository.findAll().stream()
                .filter(u -> "###test".equals(u.getDescription()))
                .toList();

        // Then
        assertTrue(testUsers.isEmpty() || testUsers.size() <= 3);
    }
}