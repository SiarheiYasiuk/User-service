package com.example.userservice.controller;

import com.example.userservice.dto.CreateUserDto;
import com.example.userservice.dto.UserDto;
import com.example.userservice.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Test
    void getAllUsers_ShouldReturnListOfUsers() {
        // Arrange
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("Test User");

        when(userService.getAllUsers()).thenReturn(List.of(userDto));

        // Act
        List<UserDto> result = userController.getAllUsers();

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Test User");
        verify(userService).getAllUsers();
    }

    @Test
    void getUserById_ShouldReturnUser() {
        // Arrange
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("Test User");

        when(userService.getUserById(1L)).thenReturn(userDto);

        // Act
        UserDto result = userController.getUserById(1L);

        // Assert
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Test User");
        verify(userService).getUserById(1L);
    }

    @Test
    void createUser_ShouldReturnCreatedUser() {
        // Arrange
        CreateUserDto createUserDto = new CreateUserDto();
        createUserDto.setName("Test User");
        createUserDto.setEmail("test@example.com");
        createUserDto.setAge(30);

        UserDto expectedUserDto = new UserDto();
        expectedUserDto.setId(1L);
        expectedUserDto.setName("Test User");

        when(userService.createUser(createUserDto)).thenReturn(expectedUserDto);

        // Act
        UserDto result = userController.createUser(createUserDto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Test User");
        verify(userService).createUser(createUserDto);
    }

    @Test
    void updateUser_ShouldReturnUpdatedUser() {
        // Arrange
        CreateUserDto updateUserDto = new CreateUserDto();
        updateUserDto.setName("Updated User");
        updateUserDto.setEmail("updated@example.com");
        updateUserDto.setAge(35);

        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("Updated User");

        when(userService.updateUser(1L, updateUserDto)).thenReturn(userDto);

        // Act
        UserDto result = userController.updateUser(1L, updateUserDto);

        // Assert
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("Updated User");
        verify(userService).updateUser(1L, updateUserDto);
    }

    @Test
    void deleteUser_ShouldCallService() {
        // Act
        userController.deleteUser(1L);

        // Assert
        verify(userService).deleteUser(1L);
    }
}