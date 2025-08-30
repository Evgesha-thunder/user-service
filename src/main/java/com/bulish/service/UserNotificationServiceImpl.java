package com.bulish.service;

import com.bulish.kafka.KafkaUserEventProducer;
import com.bulish.dto.UserDto;
import com.bulish.dto.UserOperation;
import com.bulish.dto.UserOperationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserNotificationServiceImpl implements UserNotificationService {

    private final KafkaUserEventProducer userProducer;
    private static final String USER_EVENTS_TOPIC = "user-events";
    @Override
    public void sendUserEvent(UserOperation operation, UserDto userDto) {
        UserOperationEvent event = UserOperationEvent.builder()
                .userOperation(operation)
                .email(userDto.getEmail())
                .build();

        userProducer.sendEvent(USER_EVENTS_TOPIC, userDto.getId().toString(), event);
    }
}
