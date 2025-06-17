package com.example.userservice.repository;

import com.example.userservice.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private UserRepository userRepository;

    @Test
    void existsByEmail_ShouldReturnTrue_WhenEmailExists() {
        String email = "test@example.com";
        User user = new User("Test User", email, 30);
        userRepository.save(user);

        boolean exists = userRepository.existsByEmail(email);

        assertThat(exists).isTrue();
    }

    @Test
    void existsByEmail_ShouldReturnFalse_WhenEmailDoesNotExist() {
        String nonExistingEmail = "nonexisting@example.com";

        boolean exists = userRepository.existsByEmail(nonExistingEmail);

        assertThat(exists).isFalse();
    }

    @Test
    void existsByEmail_ShouldBeCaseSensitive() {
        String email = "Test@Example.com";
        User user = new User("Test User", email, 30);
        userRepository.save(user);

        assertThat(userRepository.existsByEmail("test@example.com")).isFalse();
        assertThat(userRepository.existsByEmail("TEST@EXAMPLE.COM")).isFalse();
        assertThat(userRepository.existsByEmail(email)).isTrue();
    }

    @Test
    void saveUser_ShouldPersistUser() {
        User user = new User("New User", "new@example.com", 25);

        User savedUser = userRepository.save(user);

        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getName()).isEqualTo("New User");
        assertThat(savedUser.getEmail()).isEqualTo("new@example.com");
        assertThat(savedUser.getAge()).isEqualTo(25);
    }

    @Test
    void findById_ShouldReturnUser_WhenUserExists() {
        User user = new User("Test User", "test@example.com", 30);
        User savedUser = userRepository.save(user);

        Optional<User> foundUser = userRepository.findById(savedUser.getId());

        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getId()).isEqualTo(savedUser.getId());
        assertThat(foundUser.get().getName()).isEqualTo("Test User");
    }

    @Test
    void findById_ShouldReturnEmpty_WhenUserDoesNotExist() {
        Optional<User> result = userRepository.findById(999L);

        assertThat(result).isEmpty();
    }
}