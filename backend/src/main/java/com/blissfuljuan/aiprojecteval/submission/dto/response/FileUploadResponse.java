package com.blissfuljuan.aiprojecteval.submission.dto.response;

public record FileUploadResponse(
		SubmissionFileResponse file,
		String message
) {
}
