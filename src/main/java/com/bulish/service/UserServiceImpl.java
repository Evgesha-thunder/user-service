package com.bulish.service;

import com.bulish.dto.UserOperation;
import com.bulish.repository.UserRepository;
import com.bulish.dto.UserDto;
import com.bulish.exceptions.EmailAlreadyExistsException;
import com.bulish.exceptions.UserNotFoundException;
import com.bulish.mapper.UserMapper;
import com.bulish.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserNotificationService notificationService;

    @Override
    @Transactional
    public UserDto saveNewUser(UserDto userDto) {
        log.info("saveNewUser triggered...");

        userRepository.findByEmail(userDto.getEmail()).ifPresent(
                user -> {
                    log.error("duplicate email {}", userDto.getEmail());
                    throw new EmailAlreadyExistsException("Email " + userDto.getEmail() + " already in use");
                });

        User user = userMapper.toEntity(userDto);
        user.setCreatedAt(LocalDateTime.now());
        log.debug("new user created {}", user);

        UserDto savedUser = userMapper.toDto(userRepository.save(user));
        notificationService.sendUserEvent(UserOperation.CREATE, savedUser);

        return savedUser;
    }

    @Override
    public UserDto findById(Long id) {
        log.info("findById triggered...");

        return userRepository.findById(id)
                .map(user -> {
                    log.debug("Found user: {}", user);
                    return userMapper.toDto(user);
                })
                .orElseThrow(() -> {
                    log.error("User not found in db with id {}", id);
                    return new UserNotFoundException("User not found with id: " + id);
                });
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserDto> findAll() {
        log.info("findAll triggered...");
        List<User> users = userRepository.findAll();
        log.debug("Found users list size {}", users.size());

        return users.stream().map(userMapper::toDto).toList();
    }

    @Transactional
    @Override
    public void update(Long id, UserDto userDto) {
        log.info("update triggered...");

        userRepository.findById(id).ifPresentOrElse(
                user -> {
                    log.debug("Found user: {}", user);
                    userMapper.updateEntityFromDto(userDto, user);
                },
                () -> {
                    log.error("User not found in db with id {}", id);
                    throw new UserNotFoundException("User not found with id: " + id);
                });
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        log.info("deleteById triggered...");
        userRepository.findById(id).ifPresentOrElse(
                user -> {
                    log.debug("found user {}", user);
                    userRepository.deleteById(id);
                    notificationService.sendUserEvent(UserOperation.DELETE, userMapper.toDto(user));
                },
                () -> {
                    log.error("User not found in db with id {}", id);
                    throw new UserNotFoundException("User with id " + id + " not found in db");
                }
        );
    }
}
