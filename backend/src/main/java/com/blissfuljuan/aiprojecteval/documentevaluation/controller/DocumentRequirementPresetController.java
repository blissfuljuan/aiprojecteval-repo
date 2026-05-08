package com.blissfuljuan.aiprojecteval.documentevaluation.controller;

import com.blissfuljuan.aiprojecteval.common.response.ApiResponse;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.request.CopyPresetRequest;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.request.CreateDocumentRequirementPresetRequest;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.request.PresetDocumentRequirementRequest;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.request.UpdateDocumentRequirementPresetRequest;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.request.UpdatePresetDocumentRequirementRequest;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.response.DocumentRequirementPresetResponse;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.response.DocumentRequirementSetResponse;
import com.blissfuljuan.aiprojecteval.documentevaluation.enums.ConfigurationStatus;
import com.blissfuljuan.aiprojecteval.documentevaluation.enums.PresetVisibility;
import com.blissfuljuan.aiprojecteval.documentevaluation.service.DocumentRequirementPresetService;
import com.blissfuljuan.aiprojecteval.documentevaluation.service.DocumentRequirementSetCopyService;
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
@RequestMapping("/api/document-evaluation/presets")
public class DocumentRequirementPresetController {

	private final DocumentRequirementPresetService presetService;
	private final DocumentRequirementSetCopyService copyService;

	public DocumentRequirementPresetController(
			DocumentRequirementPresetService presetService,
			DocumentRequirementSetCopyService copyService) {
		this.presetService = presetService;
		this.copyService = copyService;
	}

	@PostMapping
	@PreAuthorize("hasRole('ADMIN')")
	public ApiResponse<DocumentRequirementPresetResponse> create(
			Authentication authentication,
			@Valid @RequestBody CreateDocumentRequirementPresetRequest request) {
		return ApiResponse.ok("Document requirement preset created",
				presetService.createPreset(authentication.getName(), request));
	}

	@GetMapping
	@PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
	public ApiResponse<List<DocumentRequirementPresetResponse>> list(
			Authentication authentication,
			@RequestParam(required = false) ConfigurationStatus status,
			@RequestParam(required = false) PresetVisibility visibility,
			@RequestParam(required = false) String category,
			@RequestParam(required = false) String search) {
		return ApiResponse.ok(presetService.listPresets(
				authentication.getName(),
				status,
				visibility,
				category,
				search));
	}

	@GetMapping("/{presetId}")
	@PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
	public ApiResponse<DocumentRequirementPresetResponse> getById(
			Authentication authentication,
			@PathVariable Long presetId) {
		return ApiResponse.ok(presetService.getPresetById(authentication.getName(), presetId));
	}

	@PostMapping("/{presetId}/copy")
	@PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
	public ApiResponse<DocumentRequirementSetResponse> copyPreset(
			Authentication authentication,
			@PathVariable Long presetId,
			@Valid @RequestBody CopyPresetRequest request) {
		return ApiResponse.ok("Document requirement set created",
				copyService.copyPresetToRequirementSet(authentication.getName(), presetId, request));
	}

	@PutMapping("/{presetId}")
	@PreAuthorize("hasRole('ADMIN')")
	public ApiResponse<DocumentRequirementPresetResponse> update(
			@PathVariable Long presetId,
			@Valid @RequestBody UpdateDocumentRequirementPresetRequest request) {
		return ApiResponse.ok("Document requirement preset updated",
				presetService.updatePreset(presetId, request));
	}

	@PatchMapping("/{presetId}/archive")
	@PreAuthorize("hasRole('ADMIN')")
	public ApiResponse<DocumentRequirementPresetResponse> archive(@PathVariable Long presetId) {
		return ApiResponse.ok("Document requirement preset archived", presetService.archivePreset(presetId));
	}

	@PatchMapping("/{presetId}/activate")
	@PreAuthorize("hasRole('ADMIN')")
	public ApiResponse<DocumentRequirementPresetResponse> activate(@PathVariable Long presetId) {
		return ApiResponse.ok("Document requirement preset activated", presetService.activatePreset(presetId));
	}

	@PostMapping("/{presetId}/document-requirements")
	@PreAuthorize("hasRole('ADMIN')")
	public ApiResponse<DocumentRequirementPresetResponse> addDocumentRequirement(
			@PathVariable Long presetId,
			@Valid @RequestBody PresetDocumentRequirementRequest request) {
		return ApiResponse.ok("Preset document requirement added",
				presetService.addDocumentRequirement(presetId, request));
	}

	@PutMapping("/{presetId}/document-requirements/{requirementId}")
	@PreAuthorize("hasRole('ADMIN')")
	public ApiResponse<DocumentRequirementPresetResponse> updateDocumentRequirement(
			@PathVariable Long presetId,
			@PathVariable Long requirementId,
			@Valid @RequestBody UpdatePresetDocumentRequirementRequest request) {
		return ApiResponse.ok("Preset document requirement updated",
				presetService.updateDocumentRequirement(presetId, requirementId, request));
	}

	@DeleteMapping("/{presetId}/document-requirements/{requirementId}")
	@PreAuthorize("hasRole('ADMIN')")
	public ApiResponse<Void> removeDocumentRequirement(
			@PathVariable Long presetId,
			@PathVariable Long requirementId) {
		presetService.removeDocumentRequirement(presetId, requirementId);
		return ApiResponse.ok("Preset document requirement removed", null);
	}
}
