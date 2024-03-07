// package com.example.todo.controller;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.ResponseEntity;
// import org.springframework.stereotype.Controller;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.bind.annotation.ResponseBody;
// import org.springframework.web.bind.annotation.RestController;

// import com.example.todo.dto.AvailableDTO;
// import com.example.todo.dto.RolesDTO;
// import com.example.todo.dto.UserDTO;
// import com.example.todo.service.UserService;

// import java.util.List;
// import org.springframework.web.bind.annotation.RequestMapping;

// //hay que llevarselo?

// @Controller
// @RequestMapping("users")
// public class UserRestController {

//     @Autowired
//     private UserService userService;

// 	@ResponseBody
// 	@GetMapping("/filter") 
// 	public List<UserDTO> filter(@RequestParam String username) { 
// 		return userService.filterByUsername(username)
// 			.stream()
// 			.map(u->{
// 				var rolesDTO = new RolesDTO(
// 					u.getRoles().contains(RolesDTO.ROLE_ADMIN),
// 					u.getRoles().contains(RolesDTO.ROLE_USER)
// 				);
// 				return new UserDTO(u.getUsername(), u.getEmail(), rolesDTO);
// 			})
// 			.toList();
// 	}

// }
