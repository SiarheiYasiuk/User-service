package com.example.userservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
@Schema(description = "Data transfer object for creating new users")
public class CreateUserDto {
    @NotBlank(message = "Name is mandatory")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    @Schema(
            description = "User's full name",
            example = "John Doe",
            requiredMode = Schema.RequiredMode.REQUIRED,
            minLength = 2,
            maxLength = 50
    )
    private String name;

    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email should be valid")
    @Schema(
            description = "User's email address",
            example = "john.doe@example.com",
            requiredMode = Schema.RequiredMode.REQUIRED,
            pattern = "^[A-Za-z0-9+_.-]+@(.+)$"
    )
    private String email;

    @NotNull(message = "Age is mandatory")
    @Min(value = 1, message = "Age must be at least 1")
    @Max(value = 120, message = "Age must be less than 120")
    @Schema(
            description = "User's age in years",
            example = "30",
            requiredMode = Schema.RequiredMode.REQUIRED,
            minimum = "1",
            maximum = "120"
    )
    private Integer age;
}