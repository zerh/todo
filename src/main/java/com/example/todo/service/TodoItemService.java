package com.example.todo.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import com.example.todo.entity.TodoItem;
import com.example.todo.entity.User;

public interface TodoItemService {
    Optional<Page<TodoItem>> findByUsername(String username, int page, int pageSize);
    Optional<TodoItem> findByUsernameAndTextValue(String username, String textValue);
    Optional<TodoItem> save(TodoItem todoItem);
    void deleteByUserAndTextValue(User user, String textValue);
    void deleteByUser(User username);
}
