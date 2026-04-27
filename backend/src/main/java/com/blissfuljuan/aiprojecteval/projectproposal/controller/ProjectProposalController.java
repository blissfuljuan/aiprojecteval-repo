package com.blissfuljuan.aiprojecteval.projectproposal.controller;

import com.blissfuljuan.aiprojecteval.common.response.ApiResponse;
import com.blissfuljuan.aiprojecteval.projectproposal.dto.AdviserDecisionRequest;
import com.blissfuljuan.aiprojecteval.projectproposal.dto.ProjectProposalCreateRequest;
import com.blissfuljuan.aiprojecteval.projectproposal.dto.ProjectProposalResponse;
import com.blissfuljuan.aiprojecteval.projectproposal.dto.ProjectProposalUpdateRequest;
import com.blissfuljuan.aiprojecteval.projectproposal.dto.ProposalDecisionRequest;
import com.blissfuljuan.aiprojecteval.projectproposal.service.ProjectProposalService;
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
@RequestMapping("/api/project-proposals")
public class ProjectProposalController {

	private final ProjectProposalService projectProposalService;

	public ProjectProposalController(ProjectProposalService projectProposalService) {
		this.projectProposalService = projectProposalService;
	}

	@PostMapping
	@PreAuthorize("hasAnyRole('STUDENT', 'ADMIN')")
	public ApiResponse<ProjectProposalResponse> create(
			Authentication authentication,
			@Valid @RequestBody ProjectProposalCreateRequest request) {
		return ApiResponse.ok("Project proposal created", projectProposalService.createProposal(authentication.getName(), request));
	}

	@GetMapping("/my")
	@PreAuthorize("hasRole('STUDENT')")
	public ApiResponse<List<ProjectProposalResponse>> getMyProposals(Authentication authentication) {
		return ApiResponse.ok(projectProposalService.getMyProposals(authentication.getName()));
	}

	@GetMapping
	@PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR', 'ADVISER')")
	public ApiResponse<List<ProjectProposalResponse>> getAllProposals(Authentication authentication) {
		return ApiResponse.ok(projectProposalService.getAllProposals(authentication.getName()));
	}

	@GetMapping("/{id}")
	public ApiResponse<ProjectProposalResponse> getById(Authentication authentication, @PathVariable Long id) {
		return ApiResponse.ok(projectProposalService.getProposalById(authentication.getName(), id));
	}

	@PutMapping("/{id}")
	public ApiResponse<ProjectProposalResponse> update(
			Authentication authentication,
			@PathVariable Long id,
			@Valid @RequestBody ProjectProposalUpdateRequest request) {
		return ApiResponse.ok("Project proposal updated", projectProposalService.updateProposal(authentication.getName(), id, request));
	}

	@DeleteMapping("/{id}")
	public ApiResponse<Void> delete(Authentication authentication, @PathVariable Long id) {
		projectProposalService.deleteProposal(authentication.getName(), id);

		return ApiResponse.ok("Project proposal deleted", null);
	}

	@PatchMapping("/{id}/adviser-decision")
	@PreAuthorize("hasAnyRole('ADMIN', 'ADVISER')")
	public ApiResponse<ProjectProposalResponse> adviserDecision(
			Authentication authentication,
			@PathVariable Long id,
			@Valid @RequestBody AdviserDecisionRequest request) {
		return ApiResponse.ok("Adviser decision recorded",
				projectProposalService.adviserDecision(authentication.getName(), id, request));
	}

	@PatchMapping("/{id}/instructor-decision")
	@PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
	public ApiResponse<ProjectProposalResponse> instructorDecision(
			Authentication authentication,
			@PathVariable Long id,
			@Valid @RequestBody ProposalDecisionRequest request) {
		return ApiResponse.ok("Project proposal decision recorded",
				projectProposalService.instructorDecision(authentication.getName(), id, request));
	}
}
