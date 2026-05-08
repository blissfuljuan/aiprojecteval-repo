package com.blissfuljuan.aiprojecteval.documentevaluation.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record DocumentEvaluationSummaryResponse(
		Long id,
		Long submissionId,
		Long documentRequirementId,
		String documentRequirementName,
		Long submittedById,
		String submittedByName,
		Long evaluatedById,
		String evaluatedByName,
		String status,
		BigDecimal totalScore,
		BigDecimal maxScore,
		BigDecimal percentageScore,
		LocalDateTime startedAt,
		LocalDateTime finalizedAt
) {
}
