package com.bulish.controller;

import com.bulish.dto.UserDto;
import com.bulish.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EntityModel<UserDto> create(@RequestBody @Valid UserDto newUser) {
        return toModel(userService.saveNewUser(newUser));
    }

    @GetMapping("/{id}")
    public EntityModel<UserDto> findUserById(@PathVariable("id") Long id) {
        return toModel(userService.findById(id));
    }

    @GetMapping
    public CollectionModel<EntityModel<UserDto>> findAllUsers() {
        List<EntityModel<UserDto>> users = userService.findAll().stream()
                .map(this::toModel)
                .collect(Collectors.toList());

        Link selfLink = linkTo(methodOn(UserController.class).findAllUsers()).withSelfRel();
        return CollectionModel.of(users, selfLink);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateUser(@PathVariable("id") Long id, @RequestBody @Valid UserDto updatedUser) {
        userService.update(id, updatedUser);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable("id") Long id) {
        userService.deleteById(id);
    }

    private EntityModel<UserDto> toModel(UserDto user) {
        EntityModel<UserDto> userModel = EntityModel.of(user);
        userModel.add(linkTo(methodOn(UserController.class).findUserById(user.getId())).withSelfRel());
        userModel.add(linkTo(methodOn(UserController.class).findAllUsers()).withRel("users"));
        userModel.add(Link.of(linkTo(UserController.class).slash(user.getId()).toString(), "update"));
        userModel.add(Link.of(linkTo(UserController.class).slash(user.getId()).toString(), "delete"));

        return userModel;
    }
}
