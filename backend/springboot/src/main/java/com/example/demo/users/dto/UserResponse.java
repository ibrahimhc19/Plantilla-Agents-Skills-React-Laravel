package com.example.demo.users.dto;

import com.example.demo.users.enums.UserRole;
import lombok.Builder;

@Builder
public record UserResponse(
    Long id,
    String email,
    String fullName,
    UserRole role,
    boolean active) {
}
