package com.example.demo.users.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.example.demo.shared.exception.ConflictException;
import com.example.demo.shared.exception.ResourceNotFoundException;
import com.example.demo.users.dto.CreateUserRequest;
import com.example.demo.users.dto.UpdateUserRequest;
import com.example.demo.users.dto.UserResponse;
import com.example.demo.users.entity.User;
import com.example.demo.users.enums.UserRole;
import com.example.demo.users.mapper.UserMapper;
import com.example.demo.users.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private UserMapper userMapper;

  @Mock
  private PasswordEncoder passwordEncoder;

  private UserService userService;

  @BeforeEach
  void setUp() {
    userService = new UserService(userRepository, userMapper, passwordEncoder);
  }

  @Test
  void createShouldPersistUser() {
    CreateUserRequest request = new CreateUserRequest(
        "new@example.com",
        "New User",
        "password123",
        UserRole.USER,
        true);

    User saved = User.builder().id(10L).email("new@example.com").fullName("New User").role(UserRole.USER).active(true)
        .build();
    UserResponse response = UserResponse.builder().id(10L).email("new@example.com").fullName("New User")
        .role(UserRole.USER).active(true).build();

    when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
    when(passwordEncoder.encode("password123")).thenReturn("encoded");
    when(userRepository.save(any(User.class))).thenReturn(saved);
    when(userMapper.toResponse(saved)).thenReturn(response);

    UserResponse result = userService.create(request);

    assertThat(result.id()).isEqualTo(10L);
    assertThat(result.email()).isEqualTo("new@example.com");
  }

  @Test
  void createShouldFailWhenEmailAlreadyExists() {
    CreateUserRequest request = new CreateUserRequest(
        "exists@example.com",
        "Existing",
        "password123",
        UserRole.USER,
        true);

    when(userRepository.existsByEmail("exists@example.com")).thenReturn(true);

    assertThatThrownBy(() -> userService.create(request))
        .isInstanceOf(ConflictException.class)
        .hasMessageContaining("Email already in use");
  }

  @Test
  void updateShouldFailWhenUserNotFound() {
    UpdateUserRequest request = new UpdateUserRequest(
        "missing@example.com",
        "Missing",
        null,
        UserRole.USER,
        true);

    when(userRepository.findById(999L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> userService.update(999L, request))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("User not found");
  }
}
