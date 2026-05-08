package com.blissfuljuan.aiprojecteval.documentevaluation.dto.request;

import jakarta.validation.constraints.Size;

public record DeactivateRequirementSetAssignmentRequest(
		@Size(max = 1000, message = "Notes must not exceed 1000 characters")
		String notes
) {
}
