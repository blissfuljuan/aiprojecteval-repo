package com.blissfuljuan.aiprojecteval.documentevaluation.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AssignRequirementSetToClassRequest(
		@NotNull(message = "Requirement set id is required")
		Long requirementSetId,

		@NotNull(message = "Course class id is required")
		Long courseClassId,

		@Size(max = 1000, message = "Notes must not exceed 1000 characters")
		String notes
) {
}
