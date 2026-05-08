package com.blissfuljuan.aiprojecteval.documentevaluation.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record RubricCriterionResponse(
		Long id,
		Long rubricId,
		String name,
		String description,
		BigDecimal maxPoints,
		BigDecimal weight,
		Integer sortOrder,
		LocalDateTime createdAt,
		LocalDateTime updatedAt,
		List<RubricLevelResponse> levels
) {
}
