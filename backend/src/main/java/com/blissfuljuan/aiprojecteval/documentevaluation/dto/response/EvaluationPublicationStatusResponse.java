package com.blissfuljuan.aiprojecteval.documentevaluation.dto.response;

import java.time.LocalDateTime;

public record EvaluationPublicationStatusResponse(
		Long evaluationId,
		String evaluationStatus,
		boolean published,
		LocalDateTime publishedAt,
		Long publishedById,
		String publishedByName,
		String publishNote,
		LocalDateTime unpublishedAt,
		Long unpublishedById,
		String unpublishedByName,
		String unpublishReason
) {
}
