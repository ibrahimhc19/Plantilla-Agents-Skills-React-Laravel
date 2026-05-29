package com.example.demo.users.service;

import com.example.demo.shared.exception.ConflictException;
import com.example.demo.shared.exception.ResourceNotFoundException;
import com.example.demo.users.dto.CreateUserRequest;
import com.example.demo.users.dto.UpdateUserRequest;
import com.example.demo.users.dto.UserResponse;
import com.example.demo.users.entity.User;
import com.example.demo.users.mapper.UserMapper;
import com.example.demo.users.repository.UserRepository;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final PasswordEncoder passwordEncoder;

  public UserService(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.userMapper = userMapper;
    this.passwordEncoder = passwordEncoder;
  }

  public UserResponse getByEmail(String email) {
    return userRepository.findByEmail(email.toLowerCase())
        .map(userMapper::toResponse)
        .orElseThrow(() -> new ResourceNotFoundException("User not found."));
  }

  public UserResponse getById(Long id) {
    return userRepository.findById(id)
        .map(userMapper::toResponse)
        .orElseThrow(() -> new ResourceNotFoundException("User not found."));
  }

  public List<UserResponse> findAll() {
    return userRepository.findAll().stream().map(userMapper::toResponse).toList();
  }

  @Transactional
  public UserResponse create(CreateUserRequest request) {
    String normalizedEmail = request.email().toLowerCase();

    if (userRepository.existsByEmail(normalizedEmail)) {
      throw new ConflictException("Email already in use.");
    }

    User user = User.builder()
        .email(normalizedEmail)
        .fullName(request.fullName())
        .passwordHash(passwordEncoder.encode(request.password()))
        .role(request.role())
        .active(request.active())
        .build();

    User saved = userRepository.save(user);
    return userMapper.toResponse(saved);
  }

  @Transactional
  public UserResponse update(Long id, UpdateUserRequest request) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("User not found."));

    String normalizedEmail = request.email().toLowerCase();
    if (userRepository.existsByEmailAndIdNot(normalizedEmail, id)) {
      throw new ConflictException("Email already in use.");
    }

    user.setEmail(normalizedEmail);
    user.setFullName(request.fullName());
    user.setRole(request.role());
    user.setActive(request.active());
    user.setUpdatedAt(OffsetDateTime.now());

    if (request.password() != null && !request.password().isBlank()) {
      user.setPasswordHash(passwordEncoder.encode(request.password()));
    }

    User updated = userRepository.save(user);
    return userMapper.toResponse(updated);
  }

  @Transactional
  public void delete(Long id) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("User not found."));
    userRepository.delete(user);
  }
}
