package com.emirhanbaran.userservice.dto;

import com.emirhanbaran.userservice.entity.Role;
import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String name,
        String email,
        Role role,
        LocalDateTime createdAt
) {}