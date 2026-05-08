package com.blissfuljuan.aiprojecteval.documentevaluation.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record ReorderDocumentRequirementsRequest(
		@NotEmpty(message = "Reorder items must not be empty")
		List<@Valid ReorderDocumentRequirementItemRequest> items
) {
}
