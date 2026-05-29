package com.example.demo.auth.service;

import com.example.demo.auth.dto.AuthResponse;
import com.example.demo.auth.dto.LoginRequest;
import com.example.demo.auth.dto.RegisterRequest;
import com.example.demo.users.dto.UserResponse;
import com.example.demo.users.entity.User;
import com.example.demo.users.mapper.UserMapper;
import com.example.demo.users.repository.UserRepository;
import com.example.demo.users.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

  private final AuthenticationManager authenticationManager;
  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final UserService userService;

  public AuthService(
      AuthenticationManager authenticationManager,
      UserRepository userRepository,
      UserMapper userMapper,
      PasswordEncoder passwordEncoder,
      JwtService jwtService,
      UserService userService) {
    this.authenticationManager = authenticationManager;
    this.userRepository = userRepository;
    this.userMapper = userMapper;
    this.passwordEncoder = passwordEncoder;
    this.jwtService = jwtService;
    this.userService = userService;
  }

  @Transactional
  public AuthResponse register(RegisterRequest request) {
    if (userRepository.existsByEmail(request.email())) {
      throw new IllegalArgumentException("Email already registered.");
    }

    User user = User.builder()
        .email(request.email().toLowerCase())
        .fullName(request.fullName())
        .passwordHash(passwordEncoder.encode(request.password()))
        .build();

    User saved = userRepository.save(user);
    return buildAuthResponse(saved);
  }

  public AuthResponse login(LoginRequest request) {
    String normalizedEmail = request.email().toLowerCase();

    authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(normalizedEmail, request.password()));

    User user = userRepository.findByEmail(normalizedEmail)
        .orElseThrow(() -> new IllegalArgumentException("Invalid credentials."));

    return buildAuthResponse(user);
  }

  public UserResponse me(String email) {
    return userService.getByEmail(email);
  }

  private AuthResponse buildAuthResponse(User user) {
    String token = jwtService.generateToken(user.getEmail());
    UserResponse userResponse = userMapper.toResponse(user);
    return AuthResponse.builder()
        .token(token)
        .user(userResponse)
        .build();
  }
}
