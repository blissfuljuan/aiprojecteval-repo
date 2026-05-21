package com.blissfuljuan.aiprojecteval.documentevaluation.dto.response;

import com.blissfuljuan.aiprojecteval.documentevaluation.enums.AllowedFileType;
import com.blissfuljuan.aiprojecteval.documentevaluation.enums.RequirementSetAssignmentType;
import com.blissfuljuan.aiprojecteval.submission.enums.SubmissionStatus;
import java.time.LocalDateTime;
import java.util.List;

public record MyAssignedDocumentRequirementResponse(
		Long assignmentId,
		Long requirementSetId,
		String requirementSetName,
		String requirementSetDescription,
		RequirementSetAssignmentType assignmentType,
		Long courseClassId,
		String courseClassName,
		Long projectId,
		String projectTitle,
		Long documentRequirementId,
		String requirementName,
		String requirementDescription,
		boolean required,
		List<AllowedFileType> allowedFileTypes,
		Integer sortOrder,
		Long existingDraftSubmissionId,
		Long latestSubmissionId,
		SubmissionStatus latestSubmissionStatus,
		LocalDateTime latestSubmittedAt,
		Integer latestAttemptNumber
) {
}
