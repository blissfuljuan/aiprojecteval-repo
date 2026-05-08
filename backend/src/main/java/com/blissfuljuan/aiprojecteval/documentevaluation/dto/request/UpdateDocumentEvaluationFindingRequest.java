package com.blissfuljuan.aiprojecteval.documentevaluation.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateDocumentEvaluationFindingRequest(
		@NotBlank(message = "Finding type is required")
		String type,

		@NotBlank(message = "Finding title is required")
		@Size(max = 150, message = "Finding title must not exceed 150 characters")
		String title,

		@Size(max = 4000, message = "Description must not exceed 4000 characters")
		String description,

		@Size(max = 4000, message = "Recommendation must not exceed 4000 characters")
		String recommendation,

		@Min(value = 0, message = "Severity must not be negative")
		@Max(value = 5, message = "Severity must not exceed 5")
		Integer severity,

		@Min(value = 0, message = "Display order must not be negative")
		Integer displayOrder
) {
}
