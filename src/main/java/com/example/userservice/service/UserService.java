package com.example.userservice.service;

import com.example.userservice.dto.UserDto;
import com.example.userservice.dto.CreateUserDto;
import com.example.userservice.entity.User;
import com.example.userservice.exception.EmailAlreadyExistsException;
import com.example.userservice.exception.UserNotFoundException;
import com.example.userservice.mapper.UserMapper;
import com.example.userservice.repository.UserRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserEventPublisher userEventPublisher;
    private final CircuitBreakerFactory circuitBreakerFactory;

    @CircuitBreaker(name = "userService", fallbackMethod = "getAllUsersFallback")
    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    private List<UserDto> getAllUsersFallback(Exception e) {
        return Collections.emptyList();
    }

    @CircuitBreaker(name = "userService", fallbackMethod = "getUserByIdFallback")
    @Transactional(readOnly = true)
    public UserDto getUserById(Long id) {
        return userMapper.toDto(getUserEntity(id));
    }

    private UserDto getUserByIdFallback(Long id, Exception e) {
        UserDto fallback = new UserDto();
        fallback.setId(id);
        fallback.setName("Service unavailable");
        fallback.setEmail("Service unavailable");
        fallback.setAge(0);
        return fallback;
    }

    @CircuitBreaker(name = "userService", fallbackMethod = "createUserFallback")
    @Transactional
    public UserDto createUser(CreateUserDto createUserDto) {
        if (userRepository.existsByEmail(createUserDto.getEmail())) {
            throw new EmailAlreadyExistsException(createUserDto.getEmail());
        }

        User user = userMapper.toEntity(createUserDto);
        User savedUser = userRepository.save(user);

        userEventPublisher.publishUserCreatedEvent(savedUser.getEmail(), savedUser.getName());

        return userMapper.toDto(savedUser);
    }

    private UserDto createUserFallback(CreateUserDto createUserDto, Exception e) {
        UserDto fallback = new UserDto();
        fallback.setName("User creation failed");
        fallback.setEmail("Service unavailable");
        fallback.setAge(0);
        return fallback;
    }

    @CircuitBreaker(name = "userService", fallbackMethod = "updateUserFallback")
    @Transactional
    public UserDto updateUser(Long id, CreateUserDto updateUserDto) {
        User user = getUserEntity(id);

        if (!user.getEmail().equals(updateUserDto.getEmail())) {
            if (userRepository.existsByEmail(updateUserDto.getEmail())) {
                throw new EmailAlreadyExistsException(updateUserDto.getEmail());
            }
        }

        user.setName(updateUserDto.getName());
        user.setEmail(updateUserDto.getEmail());
        user.setAge(updateUserDto.getAge());

        return userMapper.toDto(userRepository.save(user));
    }

    private UserDto updateUserFallback(Long id, CreateUserDto updateUserDto, Exception e) {
        UserDto fallback = new UserDto();
        fallback.setId(id);
        fallback.setName("Update failed");
        fallback.setEmail("Service unavailable");
        fallback.setAge(0);
        return fallback;
    }

    @CircuitBreaker(name = "userService", fallbackMethod = "deleteUserFallback")
    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        userEventPublisher.publishUserDeletedEvent(user.getEmail(), user.getName());

        userRepository.delete(user);
    }

    private void deleteUserFallback(Long id, Exception e) {
        log.error("Failed to delete user with id: {}", id, e);
    }

    @CircuitBreaker(name = "userService", fallbackMethod = "getUserEntityFallback")
    private User getUserEntity(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    private User getUserEntityFallback(Long id, Exception e) {
        throw new UserNotFoundException(id);
    }
}