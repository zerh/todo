package com.example.todo.dto;

import java.time.LocalDate;

public record TodoItemRequestDTO(
        int done,
        String newValue,
        String oldValue,
        String description,
        LocalDate creationDate,
        LocalDate expirationDate) {
}
