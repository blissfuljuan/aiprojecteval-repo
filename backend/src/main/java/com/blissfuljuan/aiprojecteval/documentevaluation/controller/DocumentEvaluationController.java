package com.blissfuljuan.aiprojecteval.documentevaluation.controller;

import com.blissfuljuan.aiprojecteval.common.response.ApiResponse;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.request.AddDocumentEvaluationFindingRequest;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.request.BulkUpdateCriterionScoresRequest;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.request.CompleteDocumentEvaluationRequest;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.request.ReturnDocumentEvaluationRequest;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.request.StartDocumentEvaluationRequest;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.request.UpdateCriterionScoreRequest;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.request.UpdateDocumentEvaluationFeedbackRequest;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.request.UpdateDocumentEvaluationFindingRequest;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.response.DocumentEvaluationFindingResponse;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.response.DocumentEvaluationResponse;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.response.DocumentEvaluationSummaryResponse;
import com.blissfuljuan.aiprojecteval.documentevaluation.service.DocumentEvaluationService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/document-evaluation")
public class DocumentEvaluationController {

	private final DocumentEvaluationService evaluationService;

	public DocumentEvaluationController(DocumentEvaluationService evaluationService) {
		this.evaluationService = evaluationService;
	}

	@PostMapping("/evaluations/start")
	@PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR', 'EVALUATOR')")
	public ApiResponse<DocumentEvaluationResponse> startEvaluation(
			Authentication authentication,
			@Valid @RequestBody StartDocumentEvaluationRequest request) {
		return ApiResponse.ok("Document evaluation started",
				evaluationService.startEvaluation(authentication.getName(), request));
	}

	@GetMapping("/evaluations/{evaluationId}")
	@PreAuthorize("isAuthenticated()")
	public ApiResponse<DocumentEvaluationResponse> getEvaluationById(
			Authentication authentication,
			@PathVariable Long evaluationId) {
		return ApiResponse.ok(evaluationService.getEvaluationById(authentication.getName(), evaluationId));
	}

	@GetMapping("/submissions/{submissionId}/evaluation")
	@PreAuthorize("isAuthenticated()")
	public ApiResponse<DocumentEvaluationResponse> getEvaluationBySubmission(
			Authentication authentication,
			@PathVariable Long submissionId) {
		return ApiResponse.ok(evaluationService.getEvaluationBySubmission(authentication.getName(), submissionId));
	}

	@GetMapping("/requirement-set-assignments/{assignmentId}/evaluations")
	@PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
	public ApiResponse<List<DocumentEvaluationSummaryResponse>> getEvaluationsByAssignment(
			Authentication authentication,
			@PathVariable Long assignmentId) {
		return ApiResponse.ok(evaluationService.getEvaluationsByAssignment(authentication.getName(), assignmentId));
	}

	@GetMapping("/evaluations/my-submissions")
	@PreAuthorize("isAuthenticated()")
	public ApiResponse<List<DocumentEvaluationSummaryResponse>> getMySubmittedEvaluations(
			Authentication authentication) {
		return ApiResponse.ok(evaluationService.getMySubmittedEvaluations(authentication.getName()));
	}

	@GetMapping("/evaluations/my-evaluations")
	@PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR', 'EVALUATOR')")
	public ApiResponse<List<DocumentEvaluationSummaryResponse>> getMyAssignedEvaluations(
			Authentication authentication) {
		return ApiResponse.ok(evaluationService.getMyAssignedEvaluations(authentication.getName()));
	}

	@PutMapping("/evaluations/{evaluationId}/criterion-scores/{criterionScoreId}")
	@PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR', 'EVALUATOR')")
	public ApiResponse<DocumentEvaluationResponse> updateCriterionScore(
			Authentication authentication,
			@PathVariable Long evaluationId,
			@PathVariable Long criterionScoreId,
			@Valid @RequestBody UpdateCriterionScoreRequest request) {
		return ApiResponse.ok("Criterion score updated",
				evaluationService.updateCriterionScore(
						authentication.getName(),
						evaluationId,
						criterionScoreId,
						request));
	}

	@PutMapping("/evaluations/{evaluationId}/criterion-scores")
	@PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR', 'EVALUATOR')")
	public ApiResponse<DocumentEvaluationResponse> bulkUpdateCriterionScores(
			Authentication authentication,
			@PathVariable Long evaluationId,
			@Valid @RequestBody BulkUpdateCriterionScoresRequest request) {
		return ApiResponse.ok("Criterion scores updated",
				evaluationService.bulkUpdateCriterionScores(authentication.getName(), evaluationId, request));
	}

	@PostMapping("/evaluations/{evaluationId}/findings")
	@PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR', 'EVALUATOR')")
	public ApiResponse<DocumentEvaluationResponse> addFinding(
			Authentication authentication,
			@PathVariable Long evaluationId,
			@Valid @RequestBody AddDocumentEvaluationFindingRequest request) {
		return ApiResponse.ok("Evaluation finding added",
				evaluationService.addFinding(authentication.getName(), evaluationId, request));
	}

	@PutMapping("/evaluations/{evaluationId}/findings/{findingId}")
	@PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR', 'EVALUATOR')")
	public ApiResponse<DocumentEvaluationResponse> updateFinding(
			Authentication authentication,
			@PathVariable Long evaluationId,
			@PathVariable Long findingId,
			@Valid @RequestBody UpdateDocumentEvaluationFindingRequest request) {
		return ApiResponse.ok("Evaluation finding updated",
				evaluationService.updateFinding(authentication.getName(), evaluationId, findingId, request));
	}

	@DeleteMapping("/evaluations/{evaluationId}/findings/{findingId}")
	@PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR', 'EVALUATOR')")
	public ApiResponse<DocumentEvaluationFindingResponse> deleteFinding(
			Authentication authentication,
			@PathVariable Long evaluationId,
			@PathVariable Long findingId) {
		return ApiResponse.ok("Evaluation finding deleted",
				evaluationService.deleteFinding(authentication.getName(), evaluationId, findingId));
	}

	@PutMapping("/evaluations/{evaluationId}/feedback")
	@PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR', 'EVALUATOR')")
	public ApiResponse<DocumentEvaluationResponse> updateFeedback(
			Authentication authentication,
			@PathVariable Long evaluationId,
			@Valid @RequestBody UpdateDocumentEvaluationFeedbackRequest request) {
		return ApiResponse.ok("Evaluation feedback updated",
				evaluationService.updateFeedback(authentication.getName(), evaluationId, request));
	}

	@PatchMapping("/evaluations/{evaluationId}/complete")
	@PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR', 'EVALUATOR')")
	public ApiResponse<DocumentEvaluationResponse> completeEvaluation(
			Authentication authentication,
			@PathVariable Long evaluationId,
			@Valid @RequestBody CompleteDocumentEvaluationRequest request) {
		return ApiResponse.ok("Document evaluation completed",
				evaluationService.completeEvaluation(authentication.getName(), evaluationId, request));
	}

	@PatchMapping("/evaluations/{evaluationId}/return")
	@PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR', 'EVALUATOR')")
	public ApiResponse<DocumentEvaluationResponse> returnEvaluation(
			Authentication authentication,
			@PathVariable Long evaluationId,
			@Valid @RequestBody ReturnDocumentEvaluationRequest request) {
		return ApiResponse.ok("Document evaluation returned",
				evaluationService.returnEvaluation(authentication.getName(), evaluationId, request));
	}

	@PatchMapping("/evaluations/{evaluationId}/archive")
	@PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR', 'EVALUATOR')")
	public ApiResponse<DocumentEvaluationResponse> archiveEvaluation(
			Authentication authentication,
			@PathVariable Long evaluationId) {
		return ApiResponse.ok("Document evaluation archived",
				evaluationService.archiveEvaluation(authentication.getName(), evaluationId));
	}
}
