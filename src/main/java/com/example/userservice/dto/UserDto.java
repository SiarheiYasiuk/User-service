package com.example.userservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;

@Data
@Schema(description = "User data transfer object")
public class UserDto extends RepresentationModel<UserDto> {
    @Schema(
            description = "Unique identifier of the user",
            example = "1",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long id;

    @NotBlank(message = "Name is mandatory")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    @Schema(
            description = "User's full name",
            example = "John Doe",
            minLength = 2,
            maxLength = 50
    )
    private String name;

    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email should be valid")
    @Schema(
            description = "User's email address",
            example = "john.doe@example.com",
            pattern = "^[A-Za-z0-9+_.-]+@(.+)$"
    )
    private String email;

    @NotNull(message = "Age is mandatory")
    @Min(value = 1, message = "Age must be at least 1")
    @Max(value = 120, message = "Age must be less than 120")
    @Schema(
            description = "User's age in years",
            example = "30",
            minimum = "1",
            maximum = "120"
    )
    private Integer age;

    @Schema(
            description = "Timestamp when user was created",
            example = "2023-01-01T10:00:00",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private LocalDateTime createdAt;
}