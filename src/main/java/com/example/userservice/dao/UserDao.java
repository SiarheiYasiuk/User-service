package com.example.userservice.dao;

import com.example.userservice.entity.User;
import java.util.List;

public interface UserDao {
    void save(User user);
    User findById(Long id);
    List<User> findAll();
    void update(User user);
    void delete(User user);
}