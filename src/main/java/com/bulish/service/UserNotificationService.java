package com.bulish.service;

import com.bulish.dto.UserDto;
import com.bulish.dto.UserOperation;

public interface UserNotificationService {
    void sendUserEvent(UserOperation operation, UserDto userDto);
}