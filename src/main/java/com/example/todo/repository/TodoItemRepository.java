package com.example.todo.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.todo.entity.TodoItem;
import com.example.todo.entity.User;

@Repository
public interface TodoItemRepository extends JpaRepository<TodoItem, Integer> {

    @Query("SELECT ti FROM TodoItem ti WHERE ti.user = ?1")
    Optional<Page<TodoItem>> findByUser(User user, Pageable pageable); 

    @Query("SELECT ti FROM TodoItem ti WHERE ti.user.id = :userId AND textValue = :textValue")
    Optional<TodoItem> findByUserIdAndTextValue(int userId, String textValue);

    @Modifying
    @Transactional
    @Query("DELETE FROM TodoItem ti WHERE ti.user = :user AND textValue = :textValue")
    void deleteByUserAndTextValue(User user, String textValue);

    @Modifying
    @Transactional
    @Query("DELETE FROM TodoItem ti WHERE ti.user = :user")
    void deleteByUser(User user);

}
