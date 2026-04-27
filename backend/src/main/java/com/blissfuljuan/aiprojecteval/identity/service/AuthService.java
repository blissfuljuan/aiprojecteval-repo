package com.blissfuljuan.aiprojecteval.identity.service;

import com.blissfuljuan.aiprojecteval.identity.dto.AuthResponse;
import com.blissfuljuan.aiprojecteval.identity.dto.LoginRequest;
import com.blissfuljuan.aiprojecteval.identity.dto.RegisterRequest;
import com.blissfuljuan.aiprojecteval.identity.dto.UserResponse;

public interface AuthService {

	AuthResponse register(RegisterRequest request);

	AuthResponse login(LoginRequest request);

	void logout();

	UserResponse getCurrentUser(String email);
}
