package com.blissfuljuan.aiprojecteval.documentevaluation.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record DocumentCompletenessReportResponse(
		Long assignmentId,
		Long requirementSetId,
		String requirementSetName,
		String assignmentType,
		String assignmentStatus,
		Long courseClassId,
		String courseClassName,
		Long projectId,
		String projectName,
		Long submittedById,
		String submittedByName,
		int totalRequirements,
		int requiredRequirements,
		int optionalRequirements,
		int satisfiedRequiredRequirements,
		int missingRequiredRequirements,
		int incompleteRequiredRequirements,
		boolean complete,
		boolean readyForEvaluation,
		List<DocumentRequirementCompletenessItemResponse> requirements,
		List<String> blockingIssues,
		LocalDateTime checkedAt
) {
}
