package com.bulish;

import com.bulish.dto.UserDto;
import com.bulish.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TestUserFactory {

    public static final Long USER_ID = 1L;

    public static UserDto createUserDto(Long id) {
        return UserDto.builder()
                .id(id)
                .name("Test")
                .email("test@mail.com")
                .createdAt(LocalDateTime.now())
                .age(22)
                .build();
    }

    public static UserDto createUserDto(LocalDateTime dateTime, Long id) {
        return UserDto.builder()
                .id(id)
                .name("Test")
                .email("test@mail.com")
                .createdAt(dateTime)
                .age(22)
                .build();
    }

    public static UserDto createUserDto() {
        return createUserDto(null);
    }

    public static User createUser(Long id) {
        return User.builder()
                .id(id)
                .name("Test")
                .email("test@mail.com")
                .createdAt(LocalDateTime.now())
                .age(22)
                .build();
    }

    public static User createUser(LocalDateTime dateTime, Long id) {
        return User.builder()
                .id(id)
                .name("Test")
                .email("test@mail.com")
                .createdAt(dateTime)
                .age(22)
                .build();
    }

    public static User createUser() {
        return createUser(null);
    }

    public static User createUserWithParam(String name, String email, Integer age) {
        return User.builder()
                .id(null)
                .name(name)
                .email(email)
                .createdAt(LocalDateTime.now())
                .age(age)
                .build();
    }

    public static UserDto createUserDtoWithParam(String name, String email, Integer age) {
        return UserDto.builder()
                .id(null)
                .name(name)
                .email(email)
                .createdAt(LocalDateTime.now())
                .age(age)
                .build();
    }

    public static UserDto createUserDtoWithEmail(String email) {
        return UserDto.builder()
                .id(null)
                .name("Test")
                .email(email)
                .createdAt(LocalDateTime.now())
                .age(22)
                .build();
    }

    public static UserDto createUserDtoWithAge(Integer age) {
        return UserDto.builder()
                .name("test with age")
                .email("example@mail.com")
                .createdAt(LocalDateTime.now())
                .age(age)
                .build();
    }

    public static UserDto createUserDtoWithName(String name) {
        return UserDto.builder()
                .name(name)
                .email("example@mail.com")
                .createdAt(LocalDateTime.now())
                .age(34)
                .build();
    }

    public static List<User> createLisOfUsers(int userCount) {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < userCount; i++) {
            users.add(createUserWithParam("name" + i, "email@yandex.ru" + i, i + 10));
        }
        return users;
    }
}
