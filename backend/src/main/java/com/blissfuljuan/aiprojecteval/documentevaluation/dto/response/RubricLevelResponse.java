package com.blissfuljuan.aiprojecteval.documentevaluation.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record RubricLevelResponse(
		Long id,
		Long criterionId,
		String levelName,
		String description,
		BigDecimal points,
		Integer sortOrder,
		LocalDateTime createdAt,
		LocalDateTime updatedAt
) {
}
