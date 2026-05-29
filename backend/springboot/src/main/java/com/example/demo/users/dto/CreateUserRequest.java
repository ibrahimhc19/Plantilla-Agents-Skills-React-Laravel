package com.example.demo.users.dto;

import com.example.demo.users.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(
    @NotBlank @Email String email,
    @NotBlank @Size(min = 2, max = 120) String fullName,
    @NotBlank @Size(min = 8, max = 128) String password,
    @NotNull UserRole role,
    @NotNull Boolean active) {
}
