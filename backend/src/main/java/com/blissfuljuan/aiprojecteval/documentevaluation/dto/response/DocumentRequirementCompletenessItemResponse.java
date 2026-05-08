package com.blissfuljuan.aiprojecteval.documentevaluation.dto.response;

import java.util.List;

public record DocumentRequirementCompletenessItemResponse(
		Long requirementId,
		String requirementName,
		String description,
		boolean required,
		Integer displayOrder,
		Long latestSubmissionId,
		Integer latestAttemptNumber,
		String latestSubmissionStatus,
		int uploadedFileCount,
		int validUploadedFileCount,
		boolean submitted,
		boolean hasValidFiles,
		boolean satisfied,
		boolean blocking,
		List<String> issues,
		List<DocumentCompletenessFileSummaryResponse> files
) {
}
