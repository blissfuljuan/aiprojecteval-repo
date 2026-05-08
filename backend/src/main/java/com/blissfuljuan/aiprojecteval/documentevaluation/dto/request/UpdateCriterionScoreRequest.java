package com.blissfuljuan.aiprojecteval.documentevaluation.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record UpdateCriterionScoreRequest(
		@NotNull(message = "Criterion ID is required")
		Long criterionId,

		Long selectedLevelId,

		@DecimalMin(value = "0.0", message = "Score must not be negative")
		BigDecimal score,

		@Size(max = 4000, message = "Comment must not exceed 4000 characters")
		String comment,

		@Size(max = 4000, message = "Finding must not exceed 4000 characters")
		String finding
) {
}
