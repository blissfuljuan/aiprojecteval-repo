package com.blissfuljuan.aiprojecteval.ai.dto;

public record AICriteriaScoreResponse(
		String criterion,
		Integer score,
		Integer maxScore,
		String rationale
) {
}
