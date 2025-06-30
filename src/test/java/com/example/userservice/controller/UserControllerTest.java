package com.example.userservice.controller;

import com.example.userservice.dto.CreateUserDto;
import com.example.userservice.dto.UserDto;
import com.example.userservice.service.UserService;
import com.example.userservice.assertions.UserDtoAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Test
    void getAllUsers_ShouldReturnPaginatedUsersWithLinks() {
        // Arrange
        UserDto user1 = createUserDto(1L, "User1", "user1@test.com", 25);
        UserDto user2 = createUserDto(2L, "User2", "user2@test.com", 30);
        Page<UserDto> userPage = new PageImpl<>(List.of(user1, user2),
                PageRequest.of(0, 10), 2);

        when(userService.getAllUsers()).thenReturn(List.of(user1, user2));

        // Act
        CollectionModel<EntityModel<UserDto>> result = userController.getAllUsers();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getContent().size());

        // Verify links
        assertTrue(result.getLink("self").get().getHref().contains("/api/users?page=0&size=10"));
        assertTrue(result.getLink("create-user").get().getHref().endsWith("/api/users"));

        verify(userService).getAllUsers();
    }

    @Test
    void getUserById_ShouldReturnUserWithLinks() {
        // Arrange
        UserDto userDto = createUserDto(1L, "Test User", "test@example.com", 30);
        when(userService.getUserById(1L)).thenReturn(userDto);

        // Act
        EntityModel<UserDto> result = userController.getUserById(1L);

        // Assert
        UserDtoAssert.assertThat(result)
                .hasId(1L)
                .hasName("Test User")
                .hasEmail("test@example.com")
                .hasAge(30);

        // Verify links
        assertTrue(result.getLink("self").get().getHref().endsWith("/api/users/1"));
        assertTrue(result.getLink("all-users").get().getHref().endsWith("/api/users"));
        assertTrue(result.getLink("update-user").get().getHref().endsWith("/api/users/1"));
        assertTrue(result.getLink("delete-user").get().getHref().endsWith("/api/users/1"));

        verify(userService).getUserById(1L);
    }

    @Test
    void createUser_ShouldReturnCreatedUserWithLinks() {
        // Arrange
        CreateUserDto createDto = new CreateUserDto();
        createDto.setName("New User");
        createDto.setEmail("new@example.com");
        createDto.setAge(25);

        UserDto createdUser = createUserDto(1L, "New User", "new@example.com", 25);
        when(userService.createUser(createDto)).thenReturn(createdUser);

        // Act
        ResponseEntity<EntityModel<UserDto>> response = userController.createUser(createDto);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());

        UserDtoAssert.assertThat(response.getBody())
                .hasId(1L)
                .hasName("New User")
                .hasEmail("new@example.com")
                .hasAge(25);

        // Verify location header
        assertTrue(response.getHeaders().getLocation().toString().endsWith("/api/users/1"));

        // Verify links
        assertTrue(response.getBody().getLink("self").get().getHref().endsWith("/api/users/1"));
        assertTrue(response.getBody().getLink("all-users").get().getHref().endsWith("/api/users"));

        verify(userService).createUser(createDto);
    }

    @Test
    void updateUser_ShouldReturnUpdatedUserWithLinks() {
        // Arrange
        CreateUserDto updateDto = new CreateUserDto();
        updateDto.setName("Updated User");
        updateDto.setEmail("updated@example.com");
        updateDto.setAge(30);

        UserDto updatedUser = createUserDto(1L, "Updated User", "updated@example.com", 30);
        when(userService.updateUser(1L, updateDto)).thenReturn(updatedUser);

        // Act
        EntityModel<UserDto> result = userController.updateUser(1L, updateDto);

        // Assert
        UserDtoAssert.assertThat(result)
                .hasId(1L)
                .hasName("Updated User")
                .hasEmail("updated@example.com")
                .hasAge(30);

        // Verify links
        assertTrue(result.getLink("self").get().getHref().endsWith("/api/users/1"));
        assertTrue(result.getLink("all-users").get().getHref().endsWith("/api/users"));
        assertTrue(result.getLink("delete-user").get().getHref().endsWith("/api/users/1"));

        verify(userService).updateUser(1L, updateDto);
    }

    @Test
    void deleteUser_ShouldReturnNoContent() {
        // Act
        ResponseEntity<Void> response = userController.deleteUser(1L);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(userService).deleteUser(1L);
    }

    private UserDto createUserDto(Long id, String name, String email, Integer age) {
        UserDto dto = new UserDto();
        dto.setId(id);
        dto.setName(name);
        dto.setEmail(email);
        dto.setAge(age);
        dto.setCreatedAt(java.time.LocalDateTime.now());
        return dto;
    }
}