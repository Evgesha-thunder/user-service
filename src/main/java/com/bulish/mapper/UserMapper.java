package com.bulish.mapper;

import com.bulish.dto.UserDto;
import com.bulish.model.User;

public class UserMapper {

    public User toEntity(UserDto userDto) {
        return User.builder()
                .id(userDto.getId())
                .age(userDto.getAge())
                .name(userDto.getName())
                .email(userDto.getEmail())
                .createdAt(userDto.getCreatedAt())
                .build();
    }

    public UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .age(user.getAge())
                .name(user.getName())
                .email(user.getEmail())
                .createdAt(user.getCreatedAt())
                .build();
    }
}