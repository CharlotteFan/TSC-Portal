package com.example.server.service;

import com.example.server.model.data.User;
import com.example.server.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findUserByUserId(String userId) {
        return userRepository.findByUserId(userId);
    }

    public User createUser(User user) {
        log.info("Creating new User: {}", user.getUserId());
        return userRepository.save(user);
    }
}
