package com.example.todo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager; 
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken; 
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.example.todo.dto.AuthRequestDTO;
import com.example.todo.dto.AuthResponseDTO;
import com.example.todo.dto.AvailableDTO;
import com.example.todo.dto.RolesDTO;
import com.example.todo.dto.SignupDTO;
import com.example.todo.entity.User;
import com.example.todo.service.UserService;
import com.example.todo.util.JwtUtil;

import jakarta.servlet.http.HttpServletResponse;


@Controller
@RequestMapping("/auth")
public class AuthController { 

	@Autowired
	private UserService userService;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	PasswordEncoder passwordEncoder;

	@Autowired
	UserDetailsService userDetailsService;

	@GetMapping
	public String index() {
		return "login";
	}

	@GetMapping("/signup")
	public String signup() {
		return "signup";
	}
	
	@ResponseBody
	@GetMapping("/checkAvailability") 
	public ResponseEntity<AvailableDTO> checkAvailability(@RequestParam String username) { 

		var response = userService.findByUsername(username)
			.map(u->{
				return ResponseEntity.ok(new AvailableDTO(false));
			});
        
        return response.orElse(ResponseEntity.ok(new AvailableDTO(true)));
	}

	@ResponseBody
	@PostMapping(value = "/signup", consumes = "application/json", produces = "application/json") 
	public ResponseEntity<?> singup(@RequestBody SignupDTO signupDTO) { 

		User user = new User();
		user.setUsername(signupDTO.username());
		user.setEmail(signupDTO.email());
		user.setPassword(signupDTO.password());
		user.setRoles(RolesDTO.ROLE_USER);
		userService.save(user);

		return ResponseEntity.ok().build();
	}

	@ResponseBody
    @SuppressWarnings("null")
	@PostMapping(value = "/login", consumes = "application/json", produces = "application/json") 
	public ResponseEntity<AuthResponseDTO> authenticateAndGetToken(
			@RequestBody AuthRequestDTO authRequest, 
			HttpServletResponse response) { 

		authenticationManager.authenticate(
			new UsernamePasswordAuthenticationToken(authRequest.username(), authRequest.password())
		); 

		final UserDetails user = userDetailsService.loadUserByUsername(authRequest.username());
		
		if(user!=null){	
			var authResponseDTO = new AuthResponseDTO( authRequest.username(), JwtUtil.generateToken(authRequest.username()));
			
			ResponseCookie cookie = ResponseCookie
					.from("token", authResponseDTO.token())
					.httpOnly(true)
					.maxAge(3600 * 24 * 30)
					.path("/")
					.build();

			HttpHeaders headers = new HttpHeaders();
			headers.add(HttpHeaders.SET_COOKIE, cookie.toString());

			return ResponseEntity.ok()
				.headers(headers)
				.body(authResponseDTO); 
		}

		return ResponseEntity.badRequest().build();
	}

	@GetMapping("/logout")
	public ResponseEntity<?> getMethodName(HttpServletResponse response) {
		ResponseCookie cookie = ResponseCookie
					.from("token", null)
					.httpOnly(true)
					.maxAge(0)
					.path("/")
					.build();

		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.SET_COOKIE, cookie.toString());
        headers.add(HttpHeaders.LOCATION, "/auth"); 

        return new ResponseEntity<>(headers, HttpStatus.FOUND);
	}

} 

