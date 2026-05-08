package com.blissfuljuan.aiprojecteval.documentevaluation.dto.response;

import com.blissfuljuan.aiprojecteval.identity.model.Role;

public record AssignedByResponse(
		Long id,
		String name,
		String email,
		Role role
) {
}
