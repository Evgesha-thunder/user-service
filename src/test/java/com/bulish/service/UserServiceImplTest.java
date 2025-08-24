package com.bulish.service;

import com.bulish.TestUserFactory;
import com.bulish.dto.UserDto;
import com.bulish.exceptions.EmailAlreadyExistsException;
import com.bulish.exceptions.UserNotFoundException;
import com.bulish.mapper.UserMapper;
import com.bulish.model.User;
import com.bulish.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    @DisplayName("save new user - OK")
    void saveOk() {
        User user = TestUserFactory.createUser();
        UserDto userDto = TestUserFactory.createUserDto();

        when(userRepository.findByEmail(userDto.getEmail())).thenReturn(Optional.empty());
        when(userMapper.toEntity(userDto)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userDto);

        UserDto createdUser = userService.saveNewUser(userDto);

        assertAll("created user",
                () -> assertNotNull(createdUser),
                () -> assertEquals(createdUser.getEmail(), userDto.getEmail()),
                () -> assertEquals(createdUser.getName(), userDto.getName()),
                () -> assertEquals(createdUser.getAge(), userDto.getAge())
                );

        verify(userRepository, times(1)).findByEmail(userDto.getEmail());
        verify(userMapper, times(1)).toEntity(userDto);
        verify(userRepository, times(1)).save(user);
        verify(userMapper, times(1)).toDto(user);
    }

    @Test
    @DisplayName("save new user - email already exists")
    void saveEmailAlreadyExists() {
        UserDto userDto = TestUserFactory.createUserDto();
        User user = TestUserFactory.createUser();

        when(userRepository.findByEmail(userDto.getEmail())).thenReturn(Optional.of(user));

        assertThrows(EmailAlreadyExistsException.class,
                () -> userService.saveNewUser(userDto));

        verify(userRepository, times(1)).findByEmail(userDto.getEmail());
        verifyNoMoreInteractions(userRepository);
        verifyNoMoreInteractions(userMapper);
    }

    @Test
    @DisplayName("findById - OK")
    void findByIdOk() {
        Long userId = TestUserFactory.USER_ID;
        LocalDateTime fixedTime = LocalDateTime.now();
        User user = TestUserFactory.createUser(fixedTime, userId);
        UserDto userDto = TestUserFactory.createUserDto(fixedTime, userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(userDto);

        UserDto foundUser = userService.findById(userId);

        assertAll("found user",
                () -> assertNotNull(foundUser),
                () -> assertEquals(foundUser.getCreatedAt(), user.getCreatedAt()),
                () -> assertEquals(foundUser.getAge(), user.getAge()),
                () -> assertEquals(foundUser.getName(), user.getName()),
                () -> assertEquals(foundUser.getEmail(), user.getEmail())
        );

        verify(userRepository, times(1)).findById(userId);
        verify(userMapper, times(1)).toDto(user);
        verifyNoMoreInteractions(userRepository);
        verifyNoMoreInteractions(userMapper);
    }

    @Test
    @DisplayName("findById - not found")
    void findByIdNotFound() {
        Long id = 999L;
        when(userRepository.findById(id)).thenReturn(Optional.ofNullable(null));

        assertThrows(UserNotFoundException.class,
                () -> userService.findById(id));

        verify(userRepository, times(1)).findById(id);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("findAll - OK")
    void findAllOk() {
        List<User> users = TestUserFactory.createLisOfUsers(5);
        when(userRepository.findAll()).thenReturn(users);

        List<UserDto> result = userService.findAll();

        assertThat(result).isNotEmpty();
        assertThat(result.size()).isEqualTo(users.size());

        verify(userRepository, times(1)).findAll();
        verify(userMapper, times(5)).toDto(any());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("findAll - list is empty")
    void findAllListIsEmpty() {
        when(userRepository.findAll()).thenReturn(Collections.EMPTY_LIST);

        List<UserDto> users = userService.findAll();

        assertThat(users.isEmpty());
        verify(userRepository, times(1)).findAll();
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("update - OK")
    void updateOk() {
        Long userId = TestUserFactory.USER_ID;
        UserDto userDto = TestUserFactory.createUserDto();
        userDto.setName("newName");
        User user = TestUserFactory.createUser(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.update(userId, userDto);

        verify(userRepository, times(1)).findById(userId);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("update - user not found")
    void updateUserNotFound() {
        Long userId = TestUserFactory.USER_ID;
        UserDto userDto = TestUserFactory.createUserDto();

        when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(null));

        assertThrows(UserNotFoundException.class, () -> userService.update(userId, userDto));

        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("deleteById - OK")
    void deleteById() {
        Long userId = TestUserFactory.USER_ID;
        User user = TestUserFactory.createUser(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.deleteById(userId);

        verify(userRepository, times(1)).deleteById(userId);
        verifyNoMoreInteractions(userRepository);
    }
}
