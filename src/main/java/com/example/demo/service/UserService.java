package com.example.demo.service;

import com.example.demo.entity.User;

public interface UserService {
    User saveUser(User user);
    User register(User user);
    User findByEmail(String email);
}
