package com.blissfuljuan.aiprojecteval.documentevaluation.dto.response;

public record StudentEvaluationFindingResponse(
		Long findingId,
		String type,
		String title,
		String description,
		String recommendation,
		Integer severity,
		Integer displayOrder
) {
}
