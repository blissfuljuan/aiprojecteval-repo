package com.blissfuljuan.aiprojecteval.ai.controller;

import com.blissfuljuan.aiprojecteval.ai.dto.ProjectProposalAIEvaluationRequest;
import com.blissfuljuan.aiprojecteval.ai.dto.ProjectProposalAIEvaluationResponse;
import com.blissfuljuan.aiprojecteval.ai.service.ProjectProposalAIEvaluationService;
import com.blissfuljuan.aiprojecteval.common.response.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR', 'ADVISER')")
public class ProjectProposalAIController {

	private final ProjectProposalAIEvaluationService evaluationService;

	public ProjectProposalAIController(ProjectProposalAIEvaluationService evaluationService) {
		this.evaluationService = evaluationService;
	}

	@PostMapping("/api/project-proposals/{proposalId}/ai-evaluations")
	public ApiResponse<ProjectProposalAIEvaluationResponse> evaluate(
			@PathVariable Long proposalId,
			@Valid @RequestBody ProjectProposalAIEvaluationRequest request) {
		return ApiResponse.ok("Project proposal AI evaluation completed",
				evaluationService.evaluate(proposalId, request));
	}

	@GetMapping("/api/project-proposals/{proposalId}/ai-evaluations")
	public ApiResponse<List<ProjectProposalAIEvaluationResponse>> findByProposal(@PathVariable Long proposalId) {
		return ApiResponse.ok(evaluationService.findByProposal(proposalId));
	}

	@GetMapping("/api/project-proposals/{proposalId}/ai-evaluations/latest")
	public ApiResponse<ProjectProposalAIEvaluationResponse> findLatestByProposal(@PathVariable Long proposalId) {
		return ApiResponse.ok(evaluationService.findLatestByProposal(proposalId));
	}

	@GetMapping("/api/ai/evaluations/{evaluationId}")
	public ApiResponse<ProjectProposalAIEvaluationResponse> findByEvaluationId(@PathVariable Long evaluationId) {
		return ApiResponse.ok(evaluationService.findByEvaluationId(evaluationId));
	}
}
