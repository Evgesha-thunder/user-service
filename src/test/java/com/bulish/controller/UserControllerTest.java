package com.bulish.controller;

import com.bulish.TestUserFactory;
import com.bulish.dto.UserDto;
import com.bulish.exceptions.UserNotFoundException;
import com.bulish.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private final String MAIN_PATH = "/users";
    private final String MAIN_PATH_ID = "/users/{id}";

    private final String USER_NOT_FOUND_MESSAGE = "User not found with id: ";
    private final String VALIDATION_TITLE = "Validation failed";

    @Test
    @DisplayName("POST /users - ok")
    void createOk() throws Exception {
        UserDto userDto = TestUserFactory.createUserDto();
        when(userService.saveNewUser(userDto)).thenReturn(userDto);

        mockMvc.perform(post(MAIN_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(userDto.getName()))
                .andExpect(jsonPath("$.email").value(userDto.getEmail()))
                .andExpect(jsonPath("$.age").value(userDto.getAge()));

        verify(userService, times(1)).saveNewUser(userDto);
    }

    @Test
    @DisplayName("POST /users - invalid little age")
    void createUserInvalidLittleAge() throws Exception {
        UserDto user = TestUserFactory.createUserDtoWithAge(2);

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.title").value(VALIDATION_TITLE))
                .andExpect(jsonPath("$.fieldErrors.age").exists());

        verify(userService, never()).saveNewUser(any());
    }
    @Test
    @DisplayName("POST /users - invalid email")
    void createUserInvalidEmail() throws Exception {
        UserDto user = TestUserFactory.createUserDtoWithEmail("invalid");

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.title").value(VALIDATION_TITLE))
                .andExpect(jsonPath("$.fieldErrors.email").exists());

        verify(userService, never()).saveNewUser(any());
    }

    @Test
    @DisplayName("POST /users - invalid empty name")
    void createUserEmptyName() throws Exception {
        UserDto user = TestUserFactory.createUserDtoWithName("");

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.title").value(VALIDATION_TITLE))
                .andExpect(jsonPath("$.fieldErrors.name").exists());

        verify(userService, never()).saveNewUser(any());
    }

    @Test
    @DisplayName("GET /users/{id} - found user")
    void findUserByIdFound() throws Exception {
        UserDto foundUser = TestUserFactory.createUserDto();
        Long userId = TestUserFactory.USER_ID;
        when(userService.findById(userId)).thenReturn(foundUser);

        mockMvc.perform(get(MAIN_PATH_ID, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(foundUser.getName()))
                .andExpect(jsonPath("$.email").value(foundUser.getEmail()))
                .andExpect(jsonPath("$.age").value(foundUser.getAge()));

        verify(userService, times(1)).findById(userId);
    }

    @Test
    @DisplayName("GET /users/{id} - not found user")
    void findUserByIdNotFound() throws Exception {
        Long userId = TestUserFactory.USER_ID;
        String msg = USER_NOT_FOUND_MESSAGE + userId;

        doThrow(new UserNotFoundException(msg))
                .when(userService).findById(userId);

        mockMvc.perform(MockMvcRequestBuilders.get(MAIN_PATH_ID, userId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(msg));

        verify(userService, times(1)).findById(userId);
    }

    @Test
    @DisplayName("GET /users - found users")
    void findAllUsersFound() throws Exception {
        List<UserDto> users = List.of(
                TestUserFactory.createUserDto(),
                TestUserFactory.createUserDtoWithEmail("secondUser@yandex.com")
        );
        when(userService.findAll()).thenReturn(users);

        mockMvc.perform(get(MAIN_PATH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(users.size()))
                .andExpect(jsonPath("$[0].name").value(users.getFirst().getName()))
                .andExpect(jsonPath("$[1].name").value(users.getLast().getName()))
                .andExpect(jsonPath("$[0].email").value(users.getFirst().getEmail()))
                .andExpect(jsonPath("$[1].email").value(users.getLast().getEmail()))
                .andExpect(jsonPath("$[0].age").value(users.getFirst().getAge()))
                .andExpect(jsonPath("$[1].age").value(users.getLast().getAge()));

        verify(userService, times(1)).findAll();
    }

    @Test
    @DisplayName("GET /users - empty list")
    void findAllUsersEmptyList() throws Exception {
        when(userService.findAll()).thenReturn(List.of());

        mockMvc.perform(get(MAIN_PATH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(0));

        verify(userService, times(1)).findAll();
    }

    @Test
    @DisplayName("PUT /users/{id} - no content")
    void updateUserOk() throws Exception {
        UserDto userDto = TestUserFactory.createUserDto();
        Long userId = TestUserFactory.USER_ID;

        mockMvc.perform(MockMvcRequestBuilders.put(MAIN_PATH_ID, userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).update(userId, userDto);
    }

    @Test
    @DisplayName("PUT /users/{id} - user not found")
    void updateUserNotFound() throws Exception {
        UserDto userDto = TestUserFactory.createUserDto();
        Long userId = TestUserFactory.USER_ID;
        String msg = USER_NOT_FOUND_MESSAGE + userId;

        doThrow(new UserNotFoundException(msg))
                .when(userService).update(userId, userDto);

        mockMvc.perform(MockMvcRequestBuilders.put(MAIN_PATH_ID, userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(msg));

        verify(userService, times(1)).update(userId, userDto);
    }

    @Test
    @DisplayName("DELETE /users/{id} - not content")
    void deleteUserOk() throws Exception {
        Long userId = TestUserFactory.USER_ID;

        mockMvc.perform(MockMvcRequestBuilders.delete(MAIN_PATH_ID, userId))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteById(userId);
    }

    @Test
    @DisplayName("DELETE /users/{id} - user not found")
    void deleteUserNotFound() throws Exception {
        Long userId = TestUserFactory.USER_ID;
        String msg = USER_NOT_FOUND_MESSAGE + userId;

        doThrow(new UserNotFoundException(msg))
                .when(userService).deleteById(userId);

        mockMvc.perform(MockMvcRequestBuilders.delete(MAIN_PATH_ID, userId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(msg));

        verify(userService, times(1)).deleteById(userId);
    }
}