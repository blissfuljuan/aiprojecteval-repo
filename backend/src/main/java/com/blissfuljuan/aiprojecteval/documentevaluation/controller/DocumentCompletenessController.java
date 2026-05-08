package com.blissfuljuan.aiprojecteval.documentevaluation.controller;

import com.blissfuljuan.aiprojecteval.common.response.ApiResponse;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.response.ClassAssignmentCompletenessSummaryResponse;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.response.DocumentCompletenessReportResponse;
import com.blissfuljuan.aiprojecteval.documentevaluation.service.DocumentCompletenessService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/document-evaluation")
public class DocumentCompletenessController {

	private final DocumentCompletenessService completenessService;

	public DocumentCompletenessController(DocumentCompletenessService completenessService) {
		this.completenessService = completenessService;
	}

	@GetMapping("/requirement-set-assignments/{assignmentId}/completeness/my")
	@PreAuthorize("isAuthenticated()")
	public ApiResponse<DocumentCompletenessReportResponse> getMyCompletenessReport(
			Authentication authentication,
			@PathVariable Long assignmentId) {
		return ApiResponse.ok(completenessService.getMyCompletenessReport(
				authentication.getName(),
				assignmentId));
	}

	@GetMapping("/requirement-set-assignments/{assignmentId}/completeness/users/{userId}")
	@PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
	public ApiResponse<DocumentCompletenessReportResponse> getUserCompletenessReport(
			Authentication authentication,
			@PathVariable Long assignmentId,
			@PathVariable Long userId) {
		return ApiResponse.ok(completenessService.getUserCompletenessReport(
				authentication.getName(),
				assignmentId,
				userId));
	}

	@GetMapping("/projects/{projectId}/requirement-set-assignments/{assignmentId}/completeness")
	@PreAuthorize("isAuthenticated()")
	public ApiResponse<DocumentCompletenessReportResponse> getProjectCompletenessReport(
			Authentication authentication,
			@PathVariable Long projectId,
			@PathVariable Long assignmentId) {
		return ApiResponse.ok(completenessService.getProjectCompletenessReport(
				authentication.getName(),
				projectId,
				assignmentId));
	}

	@GetMapping("/classes/{courseClassId}/requirement-set-assignments/{assignmentId}/completeness/summary")
	@PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
	public ApiResponse<ClassAssignmentCompletenessSummaryResponse> getClassCompletenessSummary(
			Authentication authentication,
			@PathVariable Long courseClassId,
			@PathVariable Long assignmentId) {
		return ApiResponse.ok(completenessService.getClassCompletenessSummary(
				authentication.getName(),
				courseClassId,
				assignmentId));
	}
}
