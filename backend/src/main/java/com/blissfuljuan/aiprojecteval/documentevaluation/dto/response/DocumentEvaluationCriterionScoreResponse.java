package com.blissfuljuan.aiprojecteval.documentevaluation.dto.response;

import java.math.BigDecimal;

public record DocumentEvaluationCriterionScoreResponse(
		Long id,
		Long criterionId,
		String criterionName,
		String criterionDescription,
		Integer displayOrder,
		Long selectedLevelId,
		String selectedLevelName,
		String selectedLevelDescription,
		BigDecimal score,
		BigDecimal maxScore,
		String comment,
		String finding
) {
}
