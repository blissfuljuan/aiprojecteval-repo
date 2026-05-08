package com.blissfuljuan.aiprojecteval.documentevaluation.dto.response;

import java.time.LocalDateTime;

public record DocumentSubmissionFileSummaryResponse(
		Long fileId,
		String originalFileName,
		String contentType,
		Long fileSize,
		String fileStatus,
		String checksumSha256,
		LocalDateTime uploadedAt
) {
}
