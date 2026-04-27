package com.blissfuljuan.aiprojecteval.identity.controller;

import com.blissfuljuan.aiprojecteval.common.response.ApiResponse;
import com.blissfuljuan.aiprojecteval.identity.dto.AuthResponse;
import com.blissfuljuan.aiprojecteval.identity.dto.LoginRequest;
import com.blissfuljuan.aiprojecteval.identity.dto.RegisterRequest;
import com.blissfuljuan.aiprojecteval.identity.dto.UserResponse;
import com.blissfuljuan.aiprojecteval.identity.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@PostMapping("/register")
	public ApiResponse<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
		return ApiResponse.ok("Registration successful", authService.register(request));
	}

	@PostMapping("/login")
	public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
		return ApiResponse.ok("Login successful", authService.login(request));
	}

	@PostMapping("/logout")
	public ApiResponse<Void> logout() {
		authService.logout();
		return ApiResponse.ok("Logged out successfully", null);
	}

	@GetMapping("/me")
	public ApiResponse<UserResponse> me(Authentication authentication) {
		return ApiResponse.ok(authService.getCurrentUser(authentication.getName()));
	}
}
