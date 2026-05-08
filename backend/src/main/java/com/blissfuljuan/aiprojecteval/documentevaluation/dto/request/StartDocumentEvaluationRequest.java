package com.blissfuljuan.aiprojecteval.documentevaluation.dto.request;

import jakarta.validation.constraints.NotNull;

public record StartDocumentEvaluationRequest(
		@NotNull(message = "Submission ID is required")
		Long submissionId
) {
}
