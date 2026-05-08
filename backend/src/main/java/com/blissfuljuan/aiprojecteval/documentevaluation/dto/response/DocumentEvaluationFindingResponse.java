package com.blissfuljuan.aiprojecteval.documentevaluation.dto.response;

public record DocumentEvaluationFindingResponse(
		Long id,
		String type,
		String title,
		String description,
		String recommendation,
		Integer severity,
		Integer displayOrder
) {
}
