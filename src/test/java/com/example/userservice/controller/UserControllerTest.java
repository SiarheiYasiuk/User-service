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

import java.util.List;

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
        userDto.setEmail("test@example.com");
        userDto.setAge(30);

        when(userService.getAllUsers()).thenReturn(List.of(userDto));

        // Act
        List<UserDto> result = userController.getAllUsers();

        // Assert
        UserDtoAssert.assertThat(result.get(0))
                .hasId(1L)
                .hasName("Test User")
                .hasEmail("test@example.com")
                .hasAge(30);
        verify(userService).getAllUsers();
    }

    @Test
    void getUserById_ShouldReturnUser() {
        // Arrange
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("Test User");
        userDto.setEmail("test@example.com");
        userDto.setAge(30);

        when(userService.getUserById(1L)).thenReturn(userDto);

        // Act
        UserDto result = userController.getUserById(1L);

        // Assert
        UserDtoAssert.assertThat(result)
                .hasId(1L)
                .hasName("Test User")
                .hasEmail("test@example.com")
                .hasAge(30);
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
        expectedUserDto.setEmail("test@example.com");
        expectedUserDto.setAge(30);

        when(userService.createUser(createUserDto)).thenReturn(expectedUserDto);

        // Act
        UserDto result = userController.createUser(createUserDto);

        // Assert
        UserDtoAssert.assertThat(result)
                .hasId(1L)
                .hasName("Test User")
                .hasEmail("test@example.com")
                .hasAge(30);
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
        userDto.setEmail("updated@example.com");
        userDto.setAge(35);

        when(userService.updateUser(1L, updateUserDto)).thenReturn(userDto);

        // Act
        UserDto result = userController.updateUser(1L, updateUserDto);

        // Assert
        UserDtoAssert.assertThat(result)
                .hasId(1L)
                .hasName("Updated User")
                .hasEmail("updated@example.com")
                .hasAge(35);
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