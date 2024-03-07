package com.example.todo.dto;

import java.time.LocalDate;

import com.example.todo.entity.TodoItem;

public record TodoItemResponseDTO(
    int done, 
    String textValue, 
    String description, 
    LocalDate creationDate, 
    LocalDate expirationDate) {

    public static TodoItemResponseDTO of(TodoItem todoItem) {
        return new TodoItemResponseDTO(
            todoItem.getDone(),
            todoItem.getTextValue(),
            todoItem.getDescription(),
            todoItem.getCreationDate(),
            todoItem.getExpirationDate());
    }
}
