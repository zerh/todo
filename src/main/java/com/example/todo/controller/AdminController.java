package com.example.todo.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.example.todo.dto.RolesDTO;
import com.example.todo.dto.UserDTO;
import com.example.todo.service.UserService;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

	@Autowired
	private UserService userService;

	@GetMapping()
	public String index() {
		return "admin";
	}

	@GetMapping("/users")
	@ResponseBody
	public List<UserDTO> users() {
		return userService.findAll()
				.stream()
				.map(u -> {
					var rolesDTO = new RolesDTO(
							u.getRoles().contains(RolesDTO.ROLE_ADMIN),
							u.getRoles().contains(RolesDTO.ROLE_USER));
					return new UserDTO(u.getUsername(), u.getEmail(), rolesDTO);
				})
				.toList();
	}

	@GetMapping("/users/filter")
	@ResponseBody
	public List<UserDTO> users(@RequestParam String username) {
		return userService.filterByUsername(username)
				.stream()
				.map(u -> {
					var rolesDTO = new RolesDTO(
							u.getRoles().contains(RolesDTO.ROLE_ADMIN),
							u.getRoles().contains(RolesDTO.ROLE_USER));
					return new UserDTO(u.getUsername(), u.getEmail(), rolesDTO);
				})
				.toList();
	}

	@PostMapping("/users")
	@ResponseBody
	public ResponseEntity<UserDTO> update(@RequestBody UserDTO userDTO) {
		return userService.findByUsername(userDTO.username())
				.map(u -> {
					u.setUsername(userDTO.username());
					u.setEmail(userDTO.email());
					u.setRoles(userDTO.roles().toString());
					userService.save(u);
					
					return ResponseEntity.ok(
						new UserDTO(
								userDTO.username(),
								userDTO.email(),
								userDTO.roles()));
				})
				.orElse(ResponseEntity.notFound().build());
	}

	@DeleteMapping("/users/{username}")
	@ResponseBody
	public ResponseEntity<?> delete(@PathVariable String username) {
		var user = userService.findByUsername(username);
		if (user == null) {
			userService.deleteByUsername(username);
			return ResponseEntity.ok().build();
		}
		return ResponseEntity.notFound().build();
	}
}
