package com.blissfuljuan.aiprojecteval.identity.dto;

import com.blissfuljuan.aiprojecteval.identity.model.Role;

public record UserResponse(
		Long id,
		String firstName,
		String middleName,
		String lastName,
		String email,
		Role role,
		boolean enabled
) {
}
