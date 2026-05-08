package com.blissfuljuan.aiprojecteval.documentevaluation.dto.response;

import java.util.List;

public record MultipleFileUploadResponse(
		Long submissionId,
		List<DocumentSubmissionFileResponse> uploadedFiles,
		int totalUploaded,
		String message
) {
}
