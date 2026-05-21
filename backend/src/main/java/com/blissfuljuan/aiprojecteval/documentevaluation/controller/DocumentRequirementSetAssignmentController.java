package com.blissfuljuan.aiprojecteval.documentevaluation.controller;

import com.blissfuljuan.aiprojecteval.common.response.ApiResponse;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.request.AssignRequirementSetToClassRequest;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.request.AssignRequirementSetToProjectRequest;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.request.DeactivateRequirementSetAssignmentRequest;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.response.DocumentRequirementSetAssignmentResponse;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.response.DocumentRequirementSetAssignmentSummaryResponse;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.response.DocumentRequirementSetResponse;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.response.MyAssignedDocumentRequirementResponse;
import com.blissfuljuan.aiprojecteval.documentevaluation.enums.RequirementSetAssignmentStatus;
import com.blissfuljuan.aiprojecteval.documentevaluation.service.DocumentRequirementSetAssignmentService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/document-evaluation")
public class DocumentRequirementSetAssignmentController {

	private final DocumentRequirementSetAssignmentService assignmentService;

	public DocumentRequirementSetAssignmentController(
			DocumentRequirementSetAssignmentService assignmentService) {
		this.assignmentService = assignmentService;
	}

	@PostMapping("/requirement-set-assignments/class")
	@PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
	public ApiResponse<DocumentRequirementSetAssignmentResponse> assignToClass(
			Authentication authentication,
			@Valid @RequestBody AssignRequirementSetToClassRequest request) {
		return ApiResponse.ok("Requirement set assigned to class",
				assignmentService.assignToClass(authentication.getName(), request));
	}

	@PostMapping("/requirement-set-assignments/project")
	@PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
	public ApiResponse<DocumentRequirementSetAssignmentResponse> assignToProject(
			Authentication authentication,
			@Valid @RequestBody AssignRequirementSetToProjectRequest request) {
		return ApiResponse.ok("Requirement set assigned to project",
				assignmentService.assignToProject(authentication.getName(), request));
	}

	@GetMapping("/requirement-set-assignments/{assignmentId}")
	@PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
	public ApiResponse<DocumentRequirementSetAssignmentResponse> getAssignmentById(
			Authentication authentication,
			@PathVariable Long assignmentId) {
		return ApiResponse.ok(assignmentService.getAssignmentById(authentication.getName(), assignmentId));
	}

	@GetMapping("/my-assigned-requirements")
	@PreAuthorize("isAuthenticated()")
	public ApiResponse<List<MyAssignedDocumentRequirementResponse>> getMyAssignedRequirements(
			Authentication authentication) {
		return ApiResponse.ok(assignmentService.getMyAssignedDocumentRequirements(authentication.getName()));
	}

	@GetMapping("/classes/{courseClassId}/requirement-set-assignments")
	@PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
	public ApiResponse<List<DocumentRequirementSetAssignmentSummaryResponse>> getAssignmentsByClass(
			Authentication authentication,
			@PathVariable Long courseClassId,
			@RequestParam(required = false) RequirementSetAssignmentStatus status) {
		return ApiResponse.ok(assignmentService.getAssignmentsByClass(
				authentication.getName(),
				courseClassId,
				status));
	}

	@GetMapping("/projects/{projectId}/requirement-set-assignments")
	@PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
	public ApiResponse<List<DocumentRequirementSetAssignmentSummaryResponse>> getAssignmentsByProject(
			Authentication authentication,
			@PathVariable Long projectId,
			@RequestParam(required = false) RequirementSetAssignmentStatus status) {
		return ApiResponse.ok(assignmentService.getAssignmentsByProject(
				authentication.getName(),
				projectId,
				status));
	}

	@GetMapping("/classes/{courseClassId}/requirement-sets/active")
	@PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
	public ApiResponse<List<DocumentRequirementSetResponse>> getActiveRequirementSetsByClass(
			Authentication authentication,
			@PathVariable Long courseClassId) {
		return ApiResponse.ok(assignmentService.getActiveRequirementSetsByClass(
				authentication.getName(),
				courseClassId));
	}

	@GetMapping("/projects/{projectId}/requirement-sets/active")
	@PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
	public ApiResponse<List<DocumentRequirementSetResponse>> getActiveRequirementSetsByProject(
			Authentication authentication,
			@PathVariable Long projectId) {
		return ApiResponse.ok(assignmentService.getActiveRequirementSetsByProject(
				authentication.getName(),
				projectId));
	}

	@PatchMapping("/requirement-set-assignments/{assignmentId}/deactivate")
	@PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
	public ApiResponse<DocumentRequirementSetAssignmentResponse> deactivateAssignment(
			Authentication authentication,
			@PathVariable Long assignmentId,
			@Valid @RequestBody(required = false) DeactivateRequirementSetAssignmentRequest request) {
		return ApiResponse.ok("Requirement set assignment deactivated",
				assignmentService.deactivateAssignment(authentication.getName(), assignmentId, request));
	}

	@PatchMapping("/requirement-set-assignments/{assignmentId}/archive")
	@PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
	public ApiResponse<DocumentRequirementSetAssignmentResponse> archiveAssignment(
			Authentication authentication,
			@PathVariable Long assignmentId) {
		return ApiResponse.ok("Requirement set assignment archived",
				assignmentService.archiveAssignment(authentication.getName(), assignmentId));
	}

	@PatchMapping("/requirement-set-assignments/{assignmentId}/reactivate")
	@PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
	public ApiResponse<DocumentRequirementSetAssignmentResponse> reactivateAssignment(
			Authentication authentication,
			@PathVariable Long assignmentId) {
		return ApiResponse.ok("Requirement set assignment reactivated",
				assignmentService.reactivateAssignment(authentication.getName(), assignmentId));
	}
}
