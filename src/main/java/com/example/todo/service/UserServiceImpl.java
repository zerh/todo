package com.example.todo.service;

import org.springframework.beans.factory.annotation.Autowired; 
import org.springframework.security.crypto.password.PasswordEncoder; 
import org.springframework.stereotype.Service;

import com.example.todo.entity.User;
import com.example.todo.repository.UserRepository;

import java.util.List;
import java.util.Optional;


@Service
public class UserServiceImpl implements UserService { 

	// @Autowired
	private UserRepository repository; 

	// @Autowired
	private PasswordEncoder passwordEncoder;

	public UserServiceImpl(UserRepository repository, PasswordEncoder passwordEncoder) {
		this.repository = repository;
		this.passwordEncoder = passwordEncoder;
	}
	
	public User save(User userInfo) { 
		if(userInfo==null)
			throw new IllegalArgumentException("An attempt to insert a null reference as a user.");

		userInfo.setPassword(passwordEncoder.encode(userInfo.getPassword()));
		return repository.save(userInfo); 
	}

	public Optional<User> findByUsername(String username) {
		return repository.findByUsername(username);
	}

	@Override
	public List<User> findAll() {
		return repository.findAll();
	}

	@Override
	public List<User> filterByUsername(String username) {
		return repository.findByUsernameContaining(username);
	}

	@Override
	public User deleteByUsername(String username) {
		return repository.deleteByUsername(username);
	} 

} 
