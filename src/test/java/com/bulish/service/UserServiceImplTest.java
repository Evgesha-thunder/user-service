package com.bulish.service;

import com.bulish.TestUserFactory;
import com.bulish.dao.UserDaoImpl;
import com.bulish.dto.UserDto;
import com.bulish.exception.EmailAlreadyExistsException;
import com.bulish.exception.UserNotFoundException;
import com.bulish.mapper.UserMapper;
import com.bulish.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserDaoImpl userDaoImpl;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @DisplayName("save new user - OK")
    @Test
    void saveOk() {
        User user = TestUserFactory.createUser();
        UserDto userDto = TestUserFactory.createUserDto();
        Long userId = TestUserFactory.USER_ID;
        when(userMapper.toEntity(userDto)).thenReturn(user);
        when(userDaoImpl.save(user)).thenReturn(userId);

        Long idCreatedUser = userService.save(userDto);

        assertThat(idCreatedUser).isGreaterThan(0);
        verify(userDaoImpl).findByEmail(userDto.getEmail());
        verify(userDaoImpl).save(user);
        verifyNoMoreInteractions(userDaoImpl);
    }

    @Test
    @DisplayName("save new user - email already exists")
    void saveEmailAlreadyExists() {
        UserDto userDto = TestUserFactory.createUserDto();
        User user = TestUserFactory.createUser();
        when(userDaoImpl.findByEmail(userDto.getEmail())).thenReturn(Optional.of(user));

        assertThrows(EmailAlreadyExistsException.class,
                () -> userService.save(userDto));

        verify(userDaoImpl).findByEmail(userDto.getEmail());
        verifyNoMoreInteractions(userDaoImpl);
        verifyNoMoreInteractions(userMapper);
    }

    @Test
    @DisplayName("findById - OK")
    void findByIdOk() {
        Long userId = TestUserFactory.USER_ID;
        LocalDateTime fixedTime = LocalDateTime.now();

        User user = TestUserFactory.createUser(fixedTime, userId);
        UserDto userDto = TestUserFactory.createUserDto(fixedTime, userId);
        when(userDaoImpl.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(userDto);

        UserDto foundUser = userService.findById(userId);

        assertAll("found user fields",
                () -> assertNotNull(foundUser),
                () -> assertEquals(foundUser.getCreatedAt(), user.getCreatedAt()),
                () -> assertEquals(foundUser.getAge(), user.getAge()),
                () -> assertEquals(foundUser.getId(), user.getId()),
                () -> assertEquals(foundUser.getName(), user.getName()),
                () -> assertEquals(foundUser.getEmail(), user.getEmail())
        );

        verify(userDaoImpl).findById(userId);
        verify(userMapper).toDto(user);
        verifyNoMoreInteractions(userDaoImpl);
        verifyNoMoreInteractions(userMapper);
    }

    @Test
    @DisplayName("findById - not found")
    void findByIdNotFound() {
        Long id = 999L;
        when(userDaoImpl.findById(id)).thenReturn(Optional.ofNullable(null));

        assertThrows(UserNotFoundException.class,
                () -> userService.findById(id));

        verify(userDaoImpl).findById(id);
        verifyNoMoreInteractions(userDaoImpl);
    }

    @Test
    @DisplayName("findAll - OK")
    void findAllOk() {
        List<User> users = TestUserFactory.createLisOfUsers(5);
        when(userDaoImpl.findAll()).thenReturn(users);

        List<UserDto> result = userService.findAll();

        assertThat(result).isNotEmpty();
        assertThat(result.size()).isEqualTo(users.size());

        verify(userDaoImpl).findAll();
        verify(userMapper, times(5)).toDto(any());
        verifyNoMoreInteractions(userDaoImpl);
    }

    @Test
    @DisplayName("findAll - list is empty")
    void findAllListIsEmpty() {
        when(userDaoImpl.findAll()).thenReturn(new ArrayList<>());

        List<UserDto> users = userService.findAll();

        assertThat(users.isEmpty());
        verify(userDaoImpl).findAll();
        verifyNoMoreInteractions(userDaoImpl);
    }

    @Test
    @DisplayName("update - OK")
    void updateOk() {
        Long userId = TestUserFactory.USER_ID;
        UserDto userDto = TestUserFactory.createUserDto(userId);
        User user = TestUserFactory.createUser(userId);
        when(userDaoImpl.findById(userId)).thenReturn(Optional.of(new User()));
        when(userMapper.toEntity(userDto)).thenReturn(user);

        userService.update(userDto);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userDaoImpl).update(captor.capture());

        User updatedUser = captor.getValue();
        assertThat(updatedUser.getName()).isEqualTo(userDto.getName());

        verify(userDaoImpl).findById(userId);
        verify(userDaoImpl).update(user);
        verify(userMapper).toEntity(userDto);
        verifyNoMoreInteractions(userDaoImpl);
        verifyNoMoreInteractions(userMapper);
    }

    @Test
    @DisplayName("update - user not found")
    void updateUserNotFound() {
        UserDto userDto = TestUserFactory.createUserDto();

        assertThrows(UserNotFoundException.class,
                () -> userService.update(userDto));

        verifyNoMoreInteractions(userDaoImpl);
        verifyNoMoreInteractions(userMapper);
    }

    @Test
    @DisplayName("deleteById - OK")
    void deleteById() {
        Long userId = TestUserFactory.USER_ID;

        userService.deleteById(TestUserFactory.USER_ID);

        verify(userDaoImpl).deleteById(userId);
        verifyNoMoreInteractions(userDaoImpl);
        verifyNoMoreInteractions(userMapper);
    }
}