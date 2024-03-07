package com.example.todo.controller;

import java.util.Optional;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.example.todo.dto.TodoItemRequestDTO;
import com.example.todo.dto.TodoItemResponseDTO;
import com.example.todo.dto.TodoItemResponseListDTO;
import com.example.todo.entity.TodoItem;
import com.example.todo.entity.User;
import com.example.todo.service.TodoItemService;
import com.example.todo.service.UserService;

@Controller
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
public class TodoController {

	private static int PAGE_SIZE = 5;

	@Autowired
	private TodoItemService todoItemService;

	@Autowired
	private UserService userService;

	@GetMapping("/todo")
	public String index() {
		return "todo";
	}

	@ResponseBody
	@GetMapping("/{username}/todo/{pageNumber}")
	public ResponseEntity<?> todos(@PathVariable String username, @PathVariable int pageNumber) {

		var response = todoItemService
				.findByUsername(username, pageNumber - 1, PAGE_SIZE)
				.map(page -> ResponseEntity.ok(
						new TodoItemResponseListDTO(
								page.getNumber(),
								page.getTotalPages(),
								page.map(t -> new TodoItemResponseDTO(
										t.getDone(),
										t.getTextValue(),
										t.getDescription(),
										t.getCreationDate(),
										t.getExpirationDate())).toList())));

		return response.orElse(ResponseEntity.notFound().build());
	}

	@ResponseBody
	@PostMapping("/{username}/todo")
	public ResponseEntity<?> upsert(
			@PathVariable String username,
			@RequestBody TodoItemRequestDTO todoItemDTO) {

		return todoItemService.findByUsernameAndTextValue(username, todoItemDTO.oldValue())
				.map(Optional::of)
				.orElseGet(() -> {
					var newTodoItem = new TodoItem();
					var user = userService.findByUsername(username)
							.orElseThrow(() -> new NoSuchElementException("Usuario " + username + " es inexistente"));
					newTodoItem.setUser(user);
					return Optional.of(newTodoItem);
				}).map(todoItem -> {
					todoItem.setDone(todoItemDTO.done());
					todoItem.setTextValue(todoItemDTO.newValue());
					todoItem.setDescription(todoItemDTO.description());
					todoItem.setCreationDate(todoItemDTO.creationDate());
					todoItem.setExpirationDate(todoItemDTO.expirationDate());
					todoItemService.save(todoItem);
					
					return ResponseEntity.ok(TodoItemResponseDTO.of(todoItem));
				}).orElse(ResponseEntity.notFound().build());
	}

	@ResponseBody
	@DeleteMapping("/{username}/{textValue}")
	public ResponseEntity<Void> delete(@PathVariable String username, @PathVariable String textValue) {
		Optional<User> userOpt = userService.findByUsername(username);
		if (userOpt.isPresent()) {
			todoItemService.deleteByUserAndTextValue(userOpt.get(), textValue);
			return ResponseEntity.ok().build();
		}
		return ResponseEntity.notFound().build();
	}

}
