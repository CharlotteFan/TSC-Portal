package com.example.server.controller;

import com.example.server.model.ApiResponse;
import com.example.server.model.data.User;
import com.example.server.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    //Post new User
    @PostMapping("/user")
    public ApiResponse<User> saveUser(@RequestBody User user) {
        log.info("Received request to save user: {}", user.getFirstName());

        User savedUser = userService.createUser(user);
        log.info("User saved successfully: {}", savedUser.getFirstName());

        return ApiResponse.success(savedUser);
    }

    @GetMapping("/user/all")
    public ApiResponse<List<User>> getAllUsers() {
        log.info("Received request to get all users");
        List<User> users = userService.findAll();

        log.info("Retrieved {} users", users.size());

        return ApiResponse.success(users);
    }

    @GetMapping("/user/{id}")
    public ApiResponse<User> getUserById(@PathVariable String id) {
        log.info("Received request to get user by id: {}", id);

        // Call the service to retrieve the user by id
        User user = userService.findUserByUserId(id);

        log.info("Retrieved user: {}", user.getFirstName());

        return ApiResponse.success(user);
    }
}

