package com.blissfuljuan.aiprojecteval.documentevaluation.dto.response;

import com.blissfuljuan.aiprojecteval.documentevaluation.enums.ConfigurationStatus;
import com.blissfuljuan.aiprojecteval.documentevaluation.enums.RubricScoringType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record EvaluationRubricResponse(
		Long id,
		String name,
		String description,
		Long sourcePresetRubricId,
		Long ownerInstructorId,
		String ownerInstructorName,
		String ownerInstructorEmail,
		BigDecimal totalPoints,
		RubricScoringType scoringType,
		ConfigurationStatus status,
		LocalDateTime createdAt,
		LocalDateTime updatedAt,
		List<RubricCriterionResponse> criteria
) {
}
