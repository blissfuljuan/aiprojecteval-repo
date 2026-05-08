package com.blissfuljuan.aiprojecteval.documentevaluation.dto.response;

public record FileUploadResponse(
		DocumentSubmissionFileResponse file,
		String message
) {
}
