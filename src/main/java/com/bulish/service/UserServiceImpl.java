package com.bulish.service;

import com.bulish.dao.UserDao;
import com.bulish.dto.UserDto;
import com.bulish.exception.EmailAlreadyExistsException;
import com.bulish.exception.UserNotFoundException;
import com.bulish.mapper.UserMapper;
import com.bulish.model.User;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserDao userDao;
    private final UserMapper userMapper;

    @Override
    public Long save(UserDto userDto) {
        if (userDao.findByEmail(userDto.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("Email " + userDto.getEmail() + " already in use");
        }
        User user = userMapper.toEntity(userDto);
        return userDao.save(user);
    }

    @Override
    public UserDto findById(Long id) {
        Optional<User> user = userDao.findById(id);

        return userMapper.toDto(user.orElseThrow(() ->
                new UserNotFoundException("User not found in db with id " + id)
        ));
    }

    @Override
    public List<UserDto> findAll() {
        List<User> users = userDao.findAll();
        return users.stream().map(userMapper::toDto).toList();
    }

    @Override
    public void update(UserDto userDto) {
        if (userDto.getId() == null || userDao.findById(userDto.getId()).isEmpty()) {
            throw new UserNotFoundException("User with id " + userDto.getId() + " not found in db");
        }
        userDao.update(userMapper.toEntity(userDto));
    }

    @Override
    public void deleteById(Long id) {
        userDao.deleteById(id);
    }
}
