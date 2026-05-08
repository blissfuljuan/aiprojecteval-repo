package com.blissfuljuan.aiprojecteval.documentevaluation.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ReorderDocumentRequirementItemRequest(
		@NotNull(message = "Requirement id is required")
		Long requirementId,

		@NotNull(message = "Sort order is required")
		@Min(value = 0, message = "Sort order must be greater than or equal to 0")
		Integer sortOrder
) {
}
