package com.blissfuljuan.aiprojecteval.documentevaluation.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AssignRequirementSetToProjectRequest(
		@NotNull(message = "Requirement set id is required")
		Long requirementSetId,

		@NotNull(message = "Project id is required")
		Long projectId,

		@Size(max = 1000, message = "Notes must not exceed 1000 characters")
		String notes
) {
}
