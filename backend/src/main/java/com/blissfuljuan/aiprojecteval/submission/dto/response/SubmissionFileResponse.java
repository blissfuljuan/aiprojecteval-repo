package com.blissfuljuan.aiprojecteval.submission.dto.response;

import com.blissfuljuan.aiprojecteval.submission.enums.SubmissionFileStatus;
import java.time.LocalDateTime;

public record SubmissionFileResponse(
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
		SubmissionFileStatus fileStatus,
		LocalDateTime uploadedAt
) {
}
