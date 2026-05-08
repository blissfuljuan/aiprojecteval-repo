package com.blissfuljuan.aiprojecteval.documentevaluation.service;

import com.blissfuljuan.aiprojecteval.documentevaluation.dto.response.ClassAssignmentCompletenessSummaryResponse;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.response.DocumentCompletenessReportResponse;

public interface DocumentCompletenessService {

	DocumentCompletenessReportResponse getMyCompletenessReport(String currentUserEmail, Long assignmentId);

	DocumentCompletenessReportResponse getUserCompletenessReport(
			String currentUserEmail,
			Long assignmentId,
			Long userId);

	DocumentCompletenessReportResponse getProjectCompletenessReport(
			String currentUserEmail,
			Long projectId,
			Long assignmentId);

	ClassAssignmentCompletenessSummaryResponse getClassCompletenessSummary(
			String currentUserEmail,
			Long courseClassId,
			Long assignmentId);
}
