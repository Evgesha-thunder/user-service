package com.bulish.service;

import com.bulish.dto.UserDto;

import java.util.List;

public interface UserService {
    Long save(UserDto user);
    UserDto findById(Long id);
    List<UserDto> findAll();
    void update(UserDto user);
    void deleteById(Long id);
}