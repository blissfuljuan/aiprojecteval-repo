package com.blissfuljuan.aiprojecteval.identity.service;

import com.blissfuljuan.aiprojecteval.common.exception.BadRequestException;
import com.blissfuljuan.aiprojecteval.common.exception.ResourceNotFoundException;
import com.blissfuljuan.aiprojecteval.common.security.JwtService;
import com.blissfuljuan.aiprojecteval.identity.dto.AuthResponse;
import com.blissfuljuan.aiprojecteval.identity.dto.LoginRequest;
import com.blissfuljuan.aiprojecteval.identity.dto.RegisterRequest;
import com.blissfuljuan.aiprojecteval.identity.dto.UserResponse;
import com.blissfuljuan.aiprojecteval.identity.mapper.UserMapper;
import com.blissfuljuan.aiprojecteval.identity.model.User;
import com.blissfuljuan.aiprojecteval.identity.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class AuthServiceImpl implements AuthService {

	private static final String TOKEN_TYPE = "Bearer";

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;

	AuthServiceImpl(
			UserRepository userRepository,
			PasswordEncoder passwordEncoder,
			AuthenticationManager authenticationManager,
			JwtService jwtService) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.authenticationManager = authenticationManager;
		this.jwtService = jwtService;
	}

	@Override
	@Transactional
	public AuthResponse register(RegisterRequest request) {
		if (userRepository.existsByEmail(request.email())) {
			throw new BadRequestException("Email is already registered");
		}

		User user = new User(
				request.firstName(),
				request.middleName(),
				request.lastName(),
				request.email(),
				passwordEncoder.encode(request.password()),
				request.role()
		);
		User savedUser = userRepository.save(user);
		String token = jwtService.generateToken(savedUser);

		return new AuthResponse(
				token,
				TOKEN_TYPE,
				jwtService.getExpirationMs(),
				UserMapper.toUserResponse(savedUser)
		);
	}

	@Override
	public AuthResponse login(LoginRequest request) {
		authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(request.email(), request.password()));

		User user = userRepository.findByEmail(request.email())
				.orElseThrow(() -> new BadRequestException("Invalid email or password"));
		String token = jwtService.generateToken(user);

		return new AuthResponse(
				token,
				TOKEN_TYPE,
				jwtService.getExpirationMs(),
				UserMapper.toUserResponse(user)
		);
	}

	@Override
	@Transactional(readOnly = true)
	public UserResponse getCurrentUser(String email) {
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));

		return UserMapper.toUserResponse(user);
	}
}
