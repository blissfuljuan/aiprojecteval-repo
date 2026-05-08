package com.blissfuljuan.aiprojecteval.documentevaluation.dto.request;

import jakarta.validation.constraints.Size;

public record PublishDocumentEvaluationRequest(
		@Size(max = 4000, message = "Publish note must not exceed 4000 characters")
		String publishNote
) {
}
