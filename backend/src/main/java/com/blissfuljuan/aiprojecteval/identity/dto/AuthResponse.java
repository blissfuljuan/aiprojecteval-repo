package com.blissfuljuan.aiprojecteval.identity.dto;

public record AuthResponse(
		String token,
		String tokenType,
		long expiresIn,
		UserResponse user
) {
}
