package com.example.todo.service;

import java.util.List;
import java.util.Optional;

import com.example.todo.entity.User; 

public interface UserService {
    Optional<User> findByUsername(String username);
    User save(User userInfo);
    List<User> findAll();
    List<User> filterByUsername(String username);
    User deleteByUsername(String username);
}
