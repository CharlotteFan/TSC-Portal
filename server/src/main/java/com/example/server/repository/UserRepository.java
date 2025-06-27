package com.example.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.server.model.data.User;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByUserId(String userId);
}
