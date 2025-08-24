package com.bulish.service;

import com.bulish.dto.UserDto;
import java.util.List;

public interface UserService {
    UserDto saveNewUser(UserDto user);
    UserDto findById(Long id);
    List<UserDto> findAll();
    void update(Long userId, UserDto user);
    void deleteById(Long id);
}