package com.bulish.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Builder
@Data
public class UserOperationEvent {
    private UserOperation userOperation;
    private String email;
}