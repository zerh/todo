package com.example.todo.dto;

import java.util.List;

public record TodoItemResponseListDTO(
    int page, 
    int totalPages, 
    List<TodoItemResponseDTO> list) {}
