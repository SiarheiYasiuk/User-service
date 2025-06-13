package com.example.userservice.service;

import com.example.userservice.dto.CreateUserDto;
import com.example.userservice.dto.UserDto;
import com.example.userservice.entity.User;
import com.example.userservice.exception.EmailAlreadyExistsException;
import com.example.userservice.exception.UserNotFoundException;
import com.example.userservice.mapper.UserMapper;
import com.example.userservice.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    @Test
    void getAllUsers_ShouldReturnListOfUserDtos() {
        User user = new User("Test", "test@example.com", 30);
        user.setId(1L);

        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("Test");

        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userMapper.toDto(user)).thenReturn(userDto);

        List<UserDto> result = userService.getAllUsers();

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(userDto);
        verify(userRepository).findAll();
        verify(userMapper).toDto(user);
    }

    @Test
    void getUserById_ShouldReturnUserDto_WhenUserExists() {
        User user = new User("Test", "test@example.com", 30);
        user.setId(1L);

        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("Test");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(userDto);

        UserDto result = userService.getUserById(1L);

        assertThat(result).isEqualTo(userDto);
        verify(userRepository).findById(1L);
        verify(userMapper).toDto(user);
    }

    @Test
    void getUserById_ShouldThrowException_WhenUserNotExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(1L))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User not found with id: 1");
        verify(userRepository).findById(1L);
    }

    @Test
    void createUser_ShouldSaveAndReturnUserDto() {
        CreateUserDto createUserDto = new CreateUserDto();
        createUserDto.setName("New");
        createUserDto.setEmail("new@example.com");
        createUserDto.setAge(25);

        User user = new User("New", "new@example.com", 25);
        user.setId(1L);

        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("New");

        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(userMapper.toEntity(createUserDto)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userDto);

        UserDto result = userService.createUser(createUserDto);

        assertThat(result).isEqualTo(userDto);
        verify(userRepository).existsByEmail("new@example.com");
        verify(userMapper).toEntity(createUserDto);
        verify(userRepository).save(user);
        verify(userMapper).toDto(user);
    }

    @Test
    void createUser_ShouldThrowException_WhenEmailExists() {
        CreateUserDto createUserDto = new CreateUserDto();
        createUserDto.setName("New");
        createUserDto.setEmail("exists@example.com");
        createUserDto.setAge(25);

        when(userRepository.existsByEmail("exists@example.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.createUser(createUserDto))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessage("Email already exists: exists@example.com");
        verify(userRepository).existsByEmail("exists@example.com");
    }

    @Test
    void updateUser_ShouldUpdateAndReturnUserDto() {
        CreateUserDto updateUserDto = new CreateUserDto();
        updateUserDto.setName("Updated");
        updateUserDto.setEmail("updated@example.com");
        updateUserDto.setAge(30);

        User existingUser = new User("Old", "old@example.com", 25);
        existingUser.setId(1L);

        User updatedUser = new User("Updated", "updated@example.com", 30);
        updatedUser.setId(1L);

        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("Updated");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByEmail("updated@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);
        when(userMapper.toDto(updatedUser)).thenReturn(userDto);

        UserDto result = userService.updateUser(1L, updateUserDto);

        assertThat(result).isEqualTo(userDto);
        verify(userRepository).findById(1L);
        verify(userRepository).existsByEmail("updated@example.com");
        verify(userRepository).save(any(User.class));
        verify(userMapper).toDto(updatedUser);
    }

    @Test
    void deleteUser_ShouldDeleteUser() {
        when(userRepository.existsById(1L)).thenReturn(true);

        userService.deleteUser(1L);

        verify(userRepository).existsById(1L);
        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteUser_ShouldThrowException_WhenUserNotExists() {
        when(userRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(() -> userService.deleteUser(1L))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User not found with id: 1");
        verify(userRepository).existsById(1L);
    }
}