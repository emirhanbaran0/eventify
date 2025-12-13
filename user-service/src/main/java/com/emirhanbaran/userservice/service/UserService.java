package com.emirhanbaran.userservice.service;

import com.emirhanbaran.userservice.dto.UserRequest;
import com.emirhanbaran.userservice.dto.UserResponse;

public interface UserService {
    UserResponse createUser(UserRequest request);
    UserResponse getUserById(Long id);
}