package com.example.todo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.example.todo.entity.User;
import com.example.todo.repository.UserRepository;

@SpringBootTest
public class UserServiceTests {

    private UserService userService;
    private UserRepository userRepositoryMock;

    @BeforeEach
    public void setUp() {
        userRepositoryMock = mock(UserRepository.class);
        userService = new UserServiceImpl(userRepositoryMock, new BCryptPasswordEncoder());
    }

    @Test
    void testFindByUsername() {
        String username = "test_user";
        var expectedUser = new User();
        expectedUser.setUsername(username);
        expectedUser.setEmail("test_user@billet.com");
        expectedUser.setRoles("ROLE_USER");
        expectedUser.setPassword("password");

        when(userRepositoryMock.findByUsername(username)).thenReturn(Optional.of(expectedUser));

        Optional<User> actualUser = userService.findByUsername(username);
        assertEquals(expectedUser, actualUser.get());
    }
}
