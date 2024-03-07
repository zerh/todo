package com.example.todo.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.example.todo.entity.TodoItem;
import com.example.todo.entity.User;
import com.example.todo.repository.TodoItemRepository;

@Service
public class TodoItemServiceImpl implements TodoItemService {

    private TodoItemRepository todoItemRepository;
    private UserService userService;

    public TodoItemServiceImpl(TodoItemRepository todoItemRepository, UserService userService) {
        this.todoItemRepository = todoItemRepository;
        this.userService = userService;
    }

    @Override
    public Optional<Page<TodoItem>> findByUsername(String username, int page, int pageSize) {
        Optional<User> u = userService.findByUsername(username);
        return todoItemRepository.findByUser(u.get(), PageRequest.of(page, pageSize));
    }

    @Override
    public Optional<TodoItem> findByUsernameAndTextValue(String username, String textValue) {
        Optional<User> u = userService.findByUsername(username);
        return todoItemRepository.findByUserIdAndTextValue(u.get().getId(), textValue);
    }

    @Override
    @SuppressWarnings("null")
    public Optional<TodoItem> save(TodoItem todoItem) {
        TodoItem todoItemResult = todoItemRepository.save(todoItem);
        return Optional.of( todoItemResult );
    }

    @Override
    public void deleteByUserAndTextValue(User user, String textValue) {
        userService
            .findByUsername(user.getUsername())
            .ifPresent(u->todoItemRepository.deleteByUserAndTextValue(u, textValue));
    }

    @Override
    public void deleteByUser(User user) {
        userService
            .findByUsername(user.getUsername())
            .ifPresent(u-> todoItemRepository.deleteByUser(u));
    }
}
