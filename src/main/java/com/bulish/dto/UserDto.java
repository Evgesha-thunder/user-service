package com.bulish.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class UserDto {

    private Long id;

    @NotBlank(message = "name should be specified")
    @Size(min=3, max=20, message = "name size should be between 3 and 20 letters")
    private String name;

    @NotBlank(message = "email should be specified")
    @Email(message = "email should have valid structure example example@yandex.ru")
    private String email;

    @Min(value = 7, message = "min valid age 7")
    @Max(value = 100, message = "max valid age 100")
    @NotNull(message = "age should be specified")
    private Integer age;

    private LocalDateTime createdAt;
}
