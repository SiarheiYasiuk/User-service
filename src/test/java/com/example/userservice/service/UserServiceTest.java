package com.example.userservice.service;

import com.example.userservice.dao.UserDao;
import com.example.userservice.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserDao userDao;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterUser() {
        userService.registerUser("Test", "test@mail.com", 25);
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userDao, times(1)).save(userCaptor.capture());

        User saved = userCaptor.getValue();
        assertEquals("Test", saved.getName());
    }

    @Test
    void testGetAllUsers() {
        when(userDao.findAll()).thenReturn(List.of(new User("A", "a@x.com", 30)));
        List<User> users = userService.getAllUsers();

        assertEquals(1, users.size());
        verify(userDao).findAll();
    }

    @Test
    void testGetUserById() {
        User user = new User("Test", "test@e.com", 20);
        user.setId(123L);
        when(userDao.findById(123L)).thenReturn(user);

        User result = userService.getUserById(123L);
        assertEquals("Test", result.getName());
    }

    @Test
    void testUpdateUser() {
        User user = new User("Update", "upd@e.com", 22);
        userService.updateUser(user);
        verify(userDao).update(user);
    }

    @Test
    void testDeleteUser() {
        User user = new User("Del", "del@e.com", 19);
        userService.deleteUser(user);
        verify(userDao).delete(user);
    }
}
