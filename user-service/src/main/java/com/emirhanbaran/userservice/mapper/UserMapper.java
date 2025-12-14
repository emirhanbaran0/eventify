package com.emirhanbaran.userservice.mapper;

import com.emirhanbaran.userservice.dto.UserRequest;
import com.emirhanbaran.userservice.dto.UserResponse;
import com.emirhanbaran.userservice.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toEntity(UserRequest request) {
        return User.builder()
                .name(request.name())
                .email(request.email())
                .role(request.role())
                .build();
    }

    public UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getCreatedAt()
        );
    }
}