package com.blissfuljuan.aiprojecteval.documentevaluation.controller;

import com.blissfuljuan.aiprojecteval.common.response.ApiResponse;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.request.AddDocumentRequirementRequest;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.request.CreateRequirementSetRequest;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.request.ReorderDocumentRequirementsRequest;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.request.UpdateDocumentRequirementRequest;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.request.UpdateRequirementSetRequest;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.response.DocumentRequirementResponse;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.response.DocumentRequirementSetResponse;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.response.DocumentRequirementSetSummaryResponse;
import com.blissfuljuan.aiprojecteval.documentevaluation.enums.ConfigurationStatus;
import com.blissfuljuan.aiprojecteval.documentevaluation.service.DocumentRequirementSetService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/document-evaluation")
public class DocumentRequirementSetController {

	private final DocumentRequirementSetService requirementSetService;

	public DocumentRequirementSetController(DocumentRequirementSetService requirementSetService) {
		this.requirementSetService = requirementSetService;
	}

	@PostMapping("/requirement-sets")
	@PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
	public ApiResponse<DocumentRequirementSetResponse> create(
			Authentication authentication,
			@Valid @RequestBody CreateRequirementSetRequest request) {
		return ApiResponse.ok("Document requirement set created",
				requirementSetService.createRequirementSet(authentication.getName(), request));
	}

	@GetMapping("/requirement-sets/my")
	@PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
	public ApiResponse<List<DocumentRequirementSetSummaryResponse>> getMyRequirementSets(
			Authentication authentication,
			@RequestParam(required = false) ConfigurationStatus status,
			@RequestParam(required = false) String keyword,
			@RequestParam(required = false) String search) {
		return ApiResponse.ok(requirementSetService.getMyRequirementSets(
				authentication.getName(),
				status,
				keyword == null ? search : keyword));
	}

	@GetMapping("/requirement-sets")
	@PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
	public ApiResponse<List<DocumentRequirementSetSummaryResponse>> getRequirementSets(
			Authentication authentication,
			@RequestParam(required = false) ConfigurationStatus status,
			@RequestParam(required = false) Long ownerInstructorId,
			@RequestParam(required = false) String keyword,
			@RequestParam(required = false) String search) {
		return ApiResponse.ok(requirementSetService.getRequirementSets(
				authentication.getName(),
				status,
				ownerInstructorId,
				keyword == null ? search : keyword));
	}

	@GetMapping("/requirement-sets/{requirementSetId}")
	@PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
	public ApiResponse<DocumentRequirementSetResponse> getById(
			Authentication authentication,
			@PathVariable Long requirementSetId) {
		return ApiResponse.ok(requirementSetService.getRequirementSetById(
				authentication.getName(),
				requirementSetId));
	}

	@PutMapping("/requirement-sets/{requirementSetId}")
	@PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
	public ApiResponse<DocumentRequirementSetResponse> update(
			Authentication authentication,
			@PathVariable Long requirementSetId,
			@Valid @RequestBody UpdateRequirementSetRequest request) {
		return ApiResponse.ok("Document requirement set updated",
				requirementSetService.updateRequirementSet(
						authentication.getName(),
						requirementSetId,
						request));
	}

	@PatchMapping("/requirement-sets/{requirementSetId}/activate")
	@PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
	public ApiResponse<DocumentRequirementSetResponse> activate(
			Authentication authentication,
			@PathVariable Long requirementSetId) {
		return ApiResponse.ok("Document requirement set activated",
				requirementSetService.activateRequirementSet(authentication.getName(), requirementSetId));
	}

	@PatchMapping("/requirement-sets/{requirementSetId}/archive")
	@PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
	public ApiResponse<DocumentRequirementSetResponse> archive(
			Authentication authentication,
			@PathVariable Long requirementSetId) {
		return ApiResponse.ok("Document requirement set archived",
				requirementSetService.archiveRequirementSet(authentication.getName(), requirementSetId));
	}

	@PostMapping("/requirement-sets/{requirementSetId}/requirements")
	@PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
	public ApiResponse<DocumentRequirementResponse> addDocumentRequirement(
			Authentication authentication,
			@PathVariable Long requirementSetId,
			@Valid @RequestBody AddDocumentRequirementRequest request) {
		return ApiResponse.ok("Document requirement added",
				requirementSetService.addDocumentRequirement(
						authentication.getName(),
						requirementSetId,
						request));
	}

	@PatchMapping("/requirement-sets/{requirementSetId}/requirements/reorder")
	@PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
	public ApiResponse<DocumentRequirementSetResponse> reorderDocumentRequirements(
			Authentication authentication,
			@PathVariable Long requirementSetId,
			@Valid @RequestBody ReorderDocumentRequirementsRequest request) {
		return ApiResponse.ok("Document requirements reordered",
				requirementSetService.reorderDocumentRequirements(
						authentication.getName(),
						requirementSetId,
						request));
	}

	@PutMapping("/requirements/{requirementId}")
	@PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
	public ApiResponse<DocumentRequirementResponse> updateDocumentRequirement(
			Authentication authentication,
			@PathVariable Long requirementId,
			@Valid @RequestBody UpdateDocumentRequirementRequest request) {
		return ApiResponse.ok("Document requirement updated",
				requirementSetService.updateDocumentRequirement(
						authentication.getName(),
						requirementId,
						request));
	}

	@DeleteMapping("/requirements/{requirementId}")
	@PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
	public ApiResponse<Void> deleteDocumentRequirement(
			Authentication authentication,
			@PathVariable Long requirementId) {
		requirementSetService.deleteDocumentRequirement(authentication.getName(), requirementId);
		return ApiResponse.ok("Document requirement deleted", null);
	}
}
