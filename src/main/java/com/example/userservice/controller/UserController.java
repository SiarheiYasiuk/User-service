package com.example.userservice.controller;

import com.example.userservice.dto.UserDto;
import com.example.userservice.dto.CreateUserDto;
import com.example.userservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "Endpoints for managing users")
public class UserController {
    private final UserService userService;

    @Operation(summary = "Get all users", description = "Retrieves a list of all users with navigation links")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of users")
    @GetMapping
    public CollectionModel<EntityModel<UserDto>> getAllUsers() {
        List<EntityModel<UserDto>> users = userService.getAllUsers().stream()
                .map(user -> EntityModel.of(user,
                        linkTo(methodOn(UserController.class).getUserById(user.getId())).withSelfRel(),
                        linkTo(UserController.class).slash(user.getId()).withRel("user-details")))
                .collect(Collectors.toList());

        Link selfLink = linkTo(methodOn(UserController.class).getAllUsers()).withSelfRel();
        Link createLink = linkTo(methodOn(UserController.class).createUser(null)).withRel("create-user");

        return CollectionModel.of(users, selfLink, createLink);
    }

    @Operation(summary = "Get user by ID", description = "Retrieves a specific user by their ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User found and returned"),
            @ApiResponse(responseCode = "404", description = "User not found with given ID")
    })
    @GetMapping("/{id}")
    public EntityModel<UserDto> getUserById(
            @Parameter(description = "ID of the user to be retrieved", required = true, example = "1")
            @PathVariable Long id) {
        UserDto user = userService.getUserById(id);

        return EntityModel.of(user,
                linkTo(methodOn(UserController.class).getUserById(id)).withSelfRel(),
                linkTo(methodOn(UserController.class).getAllUsers()).withRel("all-users"),
                linkTo(methodOn(UserController.class).updateUser(id, null)).withRel("update-user"),
                linkTo(methodOn(UserController.class).deleteUser(id)).withRel("delete-user"));
    }

    @Operation(summary = "Create a new user", description = "Creates a new user with the provided details")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "409", description = "Email already exists")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<EntityModel<UserDto>> createUser(
            @Parameter(description = "User data to create", required = true)
            @RequestBody @Valid CreateUserDto createUserDto) {
        UserDto createdUser = userService.createUser(createUserDto);

        EntityModel<UserDto> resource = EntityModel.of(createdUser,
                linkTo(methodOn(UserController.class).getUserById(createdUser.getId())).withSelfRel(),
                linkTo(methodOn(UserController.class).getAllUsers()).withRel("all-users"));

        return ResponseEntity
                .created(linkTo(methodOn(UserController.class).getUserById(createdUser.getId())).toUri())
                .body(resource);
    }

    @Operation(summary = "Update user", description = "Updates an existing user with new data")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "404", description = "User not found with given ID"),
            @ApiResponse(responseCode = "409", description = "New email already exists")
    })
    @PutMapping("/{id}")
    public EntityModel<UserDto> updateUser(
            @Parameter(description = "ID of the user to update", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "Updated user data", required = true)
            @RequestBody @Valid CreateUserDto updateUserDto) {
        UserDto updatedUser = userService.updateUser(id, updateUserDto);

        return EntityModel.of(updatedUser,
                linkTo(methodOn(UserController.class).getUserById(id)).withSelfRel(),
                linkTo(methodOn(UserController.class).getAllUsers()).withRel("all-users"),
                linkTo(methodOn(UserController.class).deleteUser(id)).withRel("delete-user"));
    }

    @Operation(summary = "Delete user", description = "Deletes a user by their ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found with given ID")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "ID of the user to delete", required = true, example = "1")
            @PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}