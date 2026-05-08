package com.blissfuljuan.aiprojecteval.documentevaluation.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record StudentEvaluationResultSummaryResponse(
		Long evaluationId,
		Long submissionId,
		Long documentRequirementId,
		String documentRequirementName,
		String evaluationStatus,
		boolean published,
		LocalDateTime publishedAt,
		BigDecimal totalScore,
		BigDecimal maxScore,
		BigDecimal percentageScore,
		String generalFeedback,
		LocalDateTime finalizedAt,
		LocalDateTime returnedAt
) {
}
