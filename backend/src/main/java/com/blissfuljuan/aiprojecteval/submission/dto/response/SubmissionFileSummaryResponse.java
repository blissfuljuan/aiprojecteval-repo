package com.blissfuljuan.aiprojecteval.submission.dto.response;

import java.time.LocalDateTime;

public record SubmissionFileSummaryResponse(
		Long fileId,
		String originalFileName,
		String contentType,
		Long fileSize,
		String fileStatus,
		String checksumSha256,
		LocalDateTime uploadedAt
) {
}
