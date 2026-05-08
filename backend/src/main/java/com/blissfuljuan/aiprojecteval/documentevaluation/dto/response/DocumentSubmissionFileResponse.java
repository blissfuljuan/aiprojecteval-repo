package com.blissfuljuan.aiprojecteval.documentevaluation.dto.response;

import com.blissfuljuan.aiprojecteval.documentevaluation.enums.DocumentSubmissionFileStatus;
import java.time.LocalDateTime;

public record DocumentSubmissionFileResponse(
		Long id,
		Long submissionId,
		String originalFileName,
		String storedFileName,
		String fileUrl,
		String downloadUrl,
		String viewUrl,
		String contentType,
		Long fileSize,
		String fileExtension,
		String checksum,
		DocumentSubmissionFileStatus fileStatus,
		LocalDateTime uploadedAt
) {
}
