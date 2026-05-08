package com.blissfuljuan.aiprojecteval.documentevaluation.dto.response;

import com.blissfuljuan.aiprojecteval.documentevaluation.enums.DocumentSubmissionStatus;
import java.time.LocalDateTime;
import java.util.List;

public record DocumentSubmissionResponse(
		Long id,
		Long assignmentId,
		Long requirementSetId,
		Long documentRequirementId,
		String documentRequirementName,
		Long projectId,
		String projectName,
		Long courseClassId,
		String courseClassName,
		Long submittedById,
		String submittedByName,
		DocumentSubmissionStatus status,
		String submissionTitle,
		String submissionNotes,
		Integer attemptNumber,
		LocalDateTime submittedAt,
		LocalDateTime lastUpdatedAt,
		List<DocumentSubmissionFileResponse> files
) {
}
