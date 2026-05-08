package com.blissfuljuan.aiprojecteval.documentevaluation.dto.request;

import jakarta.validation.constraints.Size;

public record CompleteDocumentEvaluationRequest(
		@Size(max = 4000, message = "General feedback must not exceed 4000 characters")
		String generalFeedback,

		@Size(max = 4000, message = "Evaluator remarks must not exceed 4000 characters")
		String evaluatorRemarks
) {
}
