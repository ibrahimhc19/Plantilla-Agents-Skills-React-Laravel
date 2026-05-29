package com.example.demo.auth.dto;

import com.example.demo.users.dto.UserResponse;
import lombok.Builder;

@Builder
public record AuthResponse(
    String token,
    UserResponse user) {
}
