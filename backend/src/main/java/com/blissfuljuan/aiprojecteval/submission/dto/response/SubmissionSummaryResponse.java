package com.blissfuljuan.aiprojecteval.submission.dto.response;

import com.blissfuljuan.aiprojecteval.submission.enums.SubmissionStatus;
import com.blissfuljuan.aiprojecteval.submission.enums.SubmissionType;
import java.time.LocalDateTime;

public record SubmissionSummaryResponse(
		Long id,
		SubmissionType type,
		Long assignmentId,
		Long requirementId,
		Long projectId,
		Long courseClassId,
		Long submittedById,
		String submittedByName,
		SubmissionStatus status,
		String submissionTitle,
		Integer attemptNumber,
		LocalDateTime submittedAt,
		LocalDateTime lastUpdatedAt
) {
}
