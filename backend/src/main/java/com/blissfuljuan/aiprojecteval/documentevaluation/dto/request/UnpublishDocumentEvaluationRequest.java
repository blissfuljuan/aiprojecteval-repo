package com.blissfuljuan.aiprojecteval.documentevaluation.dto.request;

import jakarta.validation.constraints.Size;

public record UnpublishDocumentEvaluationRequest(
		@Size(max = 4000, message = "Unpublish reason must not exceed 4000 characters")
		String reason
) {
}
