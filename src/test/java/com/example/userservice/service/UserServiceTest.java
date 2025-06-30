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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserEventPublisher userEventPublisher;

    @InjectMocks
    private UserService userService;

    @Test
    void getAllUsers_ShouldReturnListOfUserDtos() {
        User user = new User("Test", "test@example.com", 30);
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
    void getUserById_ShouldReturnUserDto() {
        User user = new User("Test", "test@example.com", 30);
        UserDto userDto = new UserDto();
        userDto.setName("Test");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(userDto);

        UserDto result = userService.getUserById(1L);

        assertThat(result).isEqualTo(userDto);
        verify(userRepository).findById(1L);
        verify(userMapper).toDto(user);
    }

    @Test
    void getUserById_ShouldThrowExceptionWhenNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUserById(1L))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User not found with id: 1");
    }

    @Test
    void createUser_ShouldSaveAndPublishEvent() {
        CreateUserDto createDto = new CreateUserDto();
        createDto.setEmail("new@example.com");
        createDto.setName("New");
        createDto.setAge(25);

        User user = new User("New", "new@example.com", 25);
        User savedUser = new User("New", "new@example.com", 25);
        savedUser.setId(1L);

        UserDto userDto = new UserDto();
        userDto.setId(1L);

        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(userMapper.toEntity(createDto)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(savedUser);
        when(userMapper.toDto(savedUser)).thenReturn(userDto);

        UserDto result = userService.createUser(createDto);

        assertThat(result.getId()).isEqualTo(1L);
        verify(userRepository).existsByEmail("new@example.com");
        verify(userMapper).toEntity(createDto);
        verify(userRepository).save(user);
        verify(userMapper).toDto(savedUser);
        verify(userEventPublisher).publishUserCreatedEvent("new@example.com", "New");
    }

    @Test
    void updateUser_ShouldUpdateAndReturnUser() {
        CreateUserDto updateDto = new CreateUserDto();
        updateDto.setEmail("updated@example.com");
        updateDto.setName("Updated");
        updateDto.setAge(30);

        User existingUser = new User("Old", "old@example.com", 25);
        User updatedUser = new User("Updated", "updated@example.com", 30);
        updatedUser.setId(1L);

        UserDto userDto = new UserDto();
        userDto.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.existsByEmail("updated@example.com")).thenReturn(false);
        when(userRepository.save(any())).thenReturn(updatedUser);
        when(userMapper.toDto(updatedUser)).thenReturn(userDto);

        UserDto result = userService.updateUser(1L, updateDto);

        assertThat(result.getId()).isEqualTo(1L);
        verify(userRepository).findById(1L);
        verify(userRepository).existsByEmail("updated@example.com");
        verify(userRepository).save(any());
        verify(userMapper).toDto(updatedUser);
    }

    @Test
    void deleteUser_ShouldDeleteAndPublishEvent() {
        User user = new User("Test", "test@example.com", 30);
        user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.deleteUser(1L);

        verify(userRepository).findById(1L);
        verify(userRepository).delete(user);
        verify(userEventPublisher).publishUserDeletedEvent("test@example.com", "Test");
    }
}