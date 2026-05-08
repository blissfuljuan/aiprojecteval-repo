package com.blissfuljuan.aiprojecteval.documentevaluation.dto.response;

import java.util.List;

public record ClassAssignmentCompletenessSummaryResponse(
		Long courseClassId,
		String courseClassName,
		Long assignmentId,
		Long requirementSetId,
		String requirementSetName,
		int totalTrackedSubmitters,
		int completeCount,
		int incompleteCount,
		int readyForEvaluationCount,
		List<DocumentCompletenessReportResponse> reports
) {
}
