package com.bulish.dao;

import com.bulish.model.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {
    Long save(User user);
    Optional<User> findById(Long id);
    Optional<User> findByEmail(String email);
    List<User> findAll();
    void update(User user);
    void deleteById(Long id);
}
