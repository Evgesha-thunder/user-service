package com.bulish.service;

import com.bulish.TestUserFactory;
import com.bulish.dto.UserDto;
import com.bulish.exceptions.EmailAlreadyExistsException;
import com.bulish.exceptions.UserNotFoundException;
import com.bulish.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
public class UserServiceImplIT {

    @Container
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void postgresqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
    }

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("save new user - OK")
    void saveNewUserOk() {
        UserDto userDto = TestUserFactory.createUserDto();

        UserDto createdUser = userService.saveNewUser(userDto);

        assertAll("created user",
                () -> assertNotNull(createdUser),
                () -> assertEquals(userDto.getEmail(), createdUser.getEmail()),
                () -> assertEquals(userDto.getName(), createdUser.getName()),
                () -> assertEquals(userDto.getAge(), createdUser.getAge()),
                () -> assertEquals(1, userRepository.count())
                );
    }

    @Test
    @DisplayName("save new user - duplicate email")
    void saveNewUserDuplicateEmail() {
        UserDto userDto = TestUserFactory.createUserDto();
        userService.saveNewUser(userDto);

        assertThrows(EmailAlreadyExistsException.class,
                () -> userService.saveNewUser(userDto));

        assertEquals(1, userRepository.count());
    }

    @Test
    @DisplayName("findById - ok")
    void findByIdOk() {
        UserDto createdUser = userService.saveNewUser(TestUserFactory.createUserDtoWithEmail("findtest@email.com"));

        UserDto foundUser = userService.findById(createdUser.getId());

        assertAll("found user",
                () -> assertNotNull(foundUser),
                () -> assertEquals(foundUser.getId(), createdUser.getId()),
                () -> assertEquals(foundUser.getEmail(), createdUser.getEmail()),
                () -> assertEquals(foundUser.getName(), createdUser.getName()),
                () -> assertEquals(foundUser.getAge(), createdUser.getAge()),
                () -> assertEquals(1, userRepository.count())
        );
    }

    @Test
    @DisplayName("findById - not found")
    void findByIdNotFound() {
        assertThrows(UserNotFoundException.class, () -> userService.findById(999L));
    }

    @Test
    @DisplayName("findAll - OK")
    void findAllOk() {
        userRepository.saveAll(TestUserFactory.createLisOfUsers(5));

        List<UserDto> foundUsers = userService.findAll();

        assertNotNull(foundUsers);
        assertEquals(5, foundUsers.size());
    }

    @Test
    @DisplayName("findAll - list is empty")
    void findAllListIsEmpty() {
        List<UserDto> foundUsers = userService.findAll();

        assertNotNull(foundUsers);
        assertEquals(0, foundUsers.size());
    }

    @Test
    @DisplayName("update - OK")
    void updateOk() {
        UserDto savedUser = userService.saveNewUser(TestUserFactory.createUserDto());
        UserDto updatedUserDto = TestUserFactory.createUserDtoWithParam("Updated Name","updatedEmail@email.com", 30);

        userService.update(savedUser.getId(), updatedUserDto);

        UserDto foundUser = userService.findById(savedUser.getId());

        assertAll("found user",
                () -> assertNotNull(foundUser),
                () -> assertEquals(foundUser.getEmail(), updatedUserDto.getEmail()),
                () -> assertEquals(foundUser.getName(), updatedUserDto.getName()),
                () -> assertEquals(foundUser.getAge(), updatedUserDto.getAge()),
                () -> assertEquals(1, userRepository.count())
        );
    }

    @Test
    @DisplayName("update - user not found")
    void updateUserNotFound() {
        UserDto userDto = TestUserFactory.createUserDto();

        assertThrows(UserNotFoundException.class, () -> userService.update(999L, userDto));
    }

    @Test
    @DisplayName("deleteById - OK")
    void deleteById() {
        UserDto savedUser = userService.saveNewUser(TestUserFactory.createUserDto());
        assertEquals(1, userRepository.count());

        userService.deleteById(savedUser.getId());

        assertEquals(0, userRepository.count());
        assertThrows(UserNotFoundException.class, () -> userService.findById(savedUser.getId()));
    }

    @Test
    @DisplayName("delete - user not found")
    void deleteUserNotFound() {
        assertThrows(UserNotFoundException.class, () -> userService.deleteById(999L));
    }
}
