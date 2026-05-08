package com.blissfuljuan.aiprojecteval.submission.dto.response;

import java.util.List;

public record MultipleFileUploadResponse(
		Long submissionId,
		List<SubmissionFileResponse> uploadedFiles,
		int totalUploaded,
		String message
) {
}
