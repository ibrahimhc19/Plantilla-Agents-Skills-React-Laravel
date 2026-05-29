package com.example.demo.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.example.demo.auth.dto.AuthResponse;
import com.example.demo.auth.dto.LoginRequest;
import com.example.demo.auth.dto.RegisterRequest;
import com.example.demo.users.dto.UserResponse;
import com.example.demo.users.entity.User;
import com.example.demo.users.mapper.UserMapper;
import com.example.demo.users.repository.UserRepository;
import com.example.demo.users.service.UserService;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

  @Mock
  private AuthenticationManager authenticationManager;

  @Mock
  private UserRepository userRepository;

  @Mock
  private UserMapper userMapper;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private JwtService jwtService;

  @Mock
  private UserService userService;

  private AuthService authService;

  @BeforeEach
  void setUp() {
    authService = new AuthService(
        authenticationManager,
        userRepository,
        userMapper,
        passwordEncoder,
        jwtService,
        userService);
  }

  @Test
  void registerShouldCreateUserAndReturnToken() {
    RegisterRequest request = new RegisterRequest("user@example.com", "User Test", "password123");
    User persisted = User.builder().id(1L).email("user@example.com").fullName("User Test").passwordHash("hash").build();
    UserResponse userResponse = UserResponse.builder().id(1L).email("user@example.com").fullName("User Test")
        .active(true).build();

    when(userRepository.existsByEmail("user@example.com")).thenReturn(false);
    when(passwordEncoder.encode("password123")).thenReturn("hash");
    when(userRepository.save(any(User.class))).thenReturn(persisted);
    when(userMapper.toResponse(persisted)).thenReturn(userResponse);
    when(jwtService.generateToken("user@example.com")).thenReturn("token-value");

    AuthResponse response = authService.register(request);

    assertThat(response.token()).isEqualTo("token-value");
    assertThat(response.user().email()).isEqualTo("user@example.com");
  }

  @Test
  void loginShouldFailWhenUserDoesNotExist() {
    LoginRequest request = new LoginRequest("missing@example.com", "password123");

    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
        .thenReturn(null);
    when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

    assertThatThrownBy(() -> authService.login(request))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Invalid credentials");
  }
}
