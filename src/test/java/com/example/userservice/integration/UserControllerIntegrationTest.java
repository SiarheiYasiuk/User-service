package com.example.userservice.integration;

import com.example.userservice.dto.CreateUserDto;
import com.example.userservice.dto.UserDto;
import com.example.userservice.entity.User;
import com.example.userservice.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.*;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerIntegrationTest {

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
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldCreateAndRetrieveUserWithHateoasLinks() {
        CreateUserDto createDto = new CreateUserDto();
        createDto.setName("Integration Test");
        createDto.setEmail("integration@test.com");
        createDto.setAge(30);

        ResponseEntity<EntityModel<UserDto>> createResponse = restTemplate.exchange(
                "/api/users",
                HttpMethod.POST,
                new HttpEntity<>(createDto),
                new ParameterizedTypeReference<EntityModel<UserDto>>() {});

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(createResponse.getBody()).isNotNull();
        assertThat(createResponse.getBody().getContent().getId()).isNotNull();

        assertThat(createResponse.getBody().getLink("self")).isPresent();
        assertThat(createResponse.getBody().getLink("all-users")).isPresent();

        Long userId = createResponse.getBody().getContent().getId();
        ResponseEntity<EntityModel<UserDto>> getResponse = restTemplate.exchange(
                "/api/users/" + userId,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<EntityModel<UserDto>>() {});

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody()).isNotNull();

        assertThat(getResponse.getBody().getLink("self")).isPresent();
        assertThat(getResponse.getBody().getLink("all-users")).isPresent();
        assertThat(getResponse.getBody().getLink("update-user")).isPresent();
        assertThat(getResponse.getBody().getLink("delete-user")).isPresent();
    }

    @Test
    void shouldReturnPaginatedUsers() {
        for (int i = 0; i < 5; i++) {
            User user = new User("User " + i, "user" + i + "@test.com", 20 + i);
            userRepository.save(user);
        }

        ResponseEntity<CollectionModel<EntityModel<UserDto>>> response = restTemplate.exchange(
                "/api/users?page=0&size=2",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<CollectionModel<EntityModel<UserDto>>>() {});

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getContent()).hasSize(2);

        assertThat(response.getBody().getLink("self")).isPresent();
        assertThat(response.getBody().getLink("create-user")).isPresent();
    }
}