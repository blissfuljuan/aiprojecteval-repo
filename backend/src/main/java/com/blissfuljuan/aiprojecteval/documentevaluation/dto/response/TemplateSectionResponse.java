package com.blissfuljuan.aiprojecteval.documentevaluation.dto.response;

import java.time.LocalDateTime;

public record TemplateSectionResponse(
		Long id,
		Long templateId,
		String title,
		String description,
		boolean required,
		Integer sortOrder,
		Integer minimumWordCount,
		LocalDateTime createdAt,
		LocalDateTime updatedAt
) {
}
