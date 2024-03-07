package com.example.todo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.todo.entity.User;

import java.util.List;
import java.util.Optional; 

@Repository
public interface UserRepository extends JpaRepository<User, Integer> { 
	
	Optional<User> findByUsername(String username); 

	@SuppressWarnings("null")
	public List<User> findAll();

	public List<User> findByUsernameContaining(String username);

	@Modifying
    @Transactional
    @Query("DELETE FROM User ui WHERE ui.username = :user")
    User deleteByUsername(String user);
}
