package com.blissfuljuan.aiprojecteval.identity.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.blissfuljuan.aiprojecteval.TestDataFactory;
import com.blissfuljuan.aiprojecteval.common.exception.BadRequestException;
import com.blissfuljuan.aiprojecteval.common.exception.ResourceNotFoundException;
import com.blissfuljuan.aiprojecteval.common.security.JwtService;
import com.blissfuljuan.aiprojecteval.identity.dto.AuthResponse;
import com.blissfuljuan.aiprojecteval.identity.dto.LoginRequest;
import com.blissfuljuan.aiprojecteval.identity.dto.RegisterRequest;
import com.blissfuljuan.aiprojecteval.identity.dto.UserResponse;
import com.blissfuljuan.aiprojecteval.identity.model.Role;
import com.blissfuljuan.aiprojecteval.identity.model.User;
import com.blissfuljuan.aiprojecteval.identity.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private AuthenticationManager authenticationManager;

	@Mock
	private JwtService jwtService;

	private AuthServiceImpl authService;

	@BeforeEach
	void setUp() {
		authService = new AuthServiceImpl(
				userRepository,
				passwordEncoder,
				authenticationManager,
				jwtService
		);
	}

	@Test
	void shouldRegisterUserSuccessfully() {
		RegisterRequest request = TestDataFactory.createRegisterRequest(Role.INSTRUCTOR);
		User savedUser = TestDataFactory.createUser(Role.INSTRUCTOR);
		when(userRepository.existsByEmail("admin@example.com")).thenReturn(false);
		when(passwordEncoder.encode("password123")).thenReturn("encoded-password");
		when(userRepository.save(any(User.class))).thenReturn(savedUser);
		when(jwtService.generateToken(savedUser)).thenReturn("jwt-token");
		when(jwtService.getExpirationMs()).thenReturn(86400000L);

		AuthResponse response = authService.register(request);

		assertThat(response.token()).isEqualTo("jwt-token");
		assertThat(response.user().email()).isEqualTo("admin@example.com");
		verify(userRepository).save(any(User.class));
		verify(jwtService).generateToken(savedUser);
	}

	@Test
	void shouldRejectAdminRegistration() {
		RegisterRequest request = TestDataFactory.createRegisterRequest(Role.ADMIN);

		assertThatThrownBy(() -> authService.register(request))
				.isInstanceOf(BadRequestException.class)
				.hasMessage("Admin registration is not allowed through the public registration form.");
	}

	@Test
	void shouldRejectDuplicateEmailRegistration() {
		RegisterRequest request = TestDataFactory.createRegisterRequest(Role.INSTRUCTOR);
		when(userRepository.existsByEmail("admin@example.com")).thenReturn(true);

		assertThatThrownBy(() -> authService.register(request))
				.isInstanceOf(BadRequestException.class)
				.hasMessage("Email is already registered");
	}

	@Test
	void shouldHashPasswordOnRegistration() {
		RegisterRequest request = TestDataFactory.createRegisterRequest(Role.STUDENT);
		User savedUser = TestDataFactory.createUser(Role.STUDENT);
		when(userRepository.existsByEmail("admin@example.com")).thenReturn(false);
		when(passwordEncoder.encode("password123")).thenReturn("encoded-password");
		when(userRepository.save(any(User.class))).thenReturn(savedUser);
		when(jwtService.generateToken(savedUser)).thenReturn("jwt-token");
		when(jwtService.getExpirationMs()).thenReturn(86400000L);

		authService.register(request);

		ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
		verify(passwordEncoder).encode("password123");
		verify(userRepository).save(userCaptor.capture());
		assertThat(userCaptor.getValue().getPassword()).isEqualTo("encoded-password");
	}

	@Test
	void shouldLoginSuccessfully() {
		LoginRequest request = TestDataFactory.createLoginRequest();
		User user = TestDataFactory.createUser(Role.ADMIN);
		when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(user));
		when(jwtService.generateToken(user)).thenReturn("jwt-token");
		when(jwtService.getExpirationMs()).thenReturn(86400000L);

		AuthResponse response = authService.login(request);

		assertThat(response.token()).isEqualTo("jwt-token");
		verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
		verify(jwtService).generateToken(user);
	}

	@Test
	void shouldLogoutWithoutServerSideState() {
		authService.logout();
	}

	@Test
	void shouldGetCurrentUserSuccessfully() {
		User user = TestDataFactory.createUser(Role.STUDENT);
		when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(user));

		UserResponse response = authService.getCurrentUser("admin@example.com");

		assertThat(response.email()).isEqualTo("admin@example.com");
		assertThat(response.role()).isEqualTo(Role.STUDENT);
	}

	@Test
	void shouldThrowWhenCurrentUserNotFound() {
		when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

		assertThatThrownBy(() -> authService.getCurrentUser("missing@example.com"))
				.isInstanceOf(ResourceNotFoundException.class)
				.hasMessage("User not found");
	}
}
