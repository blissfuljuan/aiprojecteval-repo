package com.blissfuljuan.aiprojecteval.document.controller;

import com.blissfuljuan.aiprojecteval.common.response.ApiResponse;
import com.blissfuljuan.aiprojecteval.document.dto.DocumentLinkSubmitRequest;
import com.blissfuljuan.aiprojecteval.document.dto.DocumentResponse;
import com.blissfuljuan.aiprojecteval.document.dto.DocumentSummaryResponse;
import com.blissfuljuan.aiprojecteval.document.dto.DocumentVersionResponse;
import com.blissfuljuan.aiprojecteval.document.model.DocumentContextType;
import com.blissfuljuan.aiprojecteval.document.service.DocumentService;
import com.blissfuljuan.aiprojecteval.projectproposal.service.ProjectProposalService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/documents")
@PreAuthorize("isAuthenticated()")
public class DocumentController {

	private final DocumentService documentService;
	private final ProjectProposalService projectProposalService;

	public DocumentController(DocumentService documentService, ProjectProposalService projectProposalService) {
		this.documentService = documentService;
		this.projectProposalService = projectProposalService;
	}

	@PostMapping("/link")
	public ApiResponse<DocumentResponse> submitExternalLink(
			Authentication authentication,
			@Valid @RequestBody DocumentLinkSubmitRequest request) {
		validateCanManageContext(authentication, request.contextType(), request.contextId());

		return ApiResponse.ok("Document link submitted", documentService.submitExternalLink(request));
	}

	@GetMapping
	@PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR', 'ADVISER')")
	public ApiResponse<List<DocumentSummaryResponse>> findAll() {
		return ApiResponse.ok(documentService.findAll());
	}

	@GetMapping("/{documentId}")
	public ApiResponse<DocumentResponse> findById(Authentication authentication, @PathVariable Long documentId) {
		DocumentResponse document = documentService.findById(documentId);
		validateCanViewContext(authentication, document.contextType(), document.contextId());

		return ApiResponse.ok(document);
	}

	@GetMapping("/{documentId}/versions")
	public ApiResponse<List<DocumentVersionResponse>> findVersions(Authentication authentication, @PathVariable Long documentId) {
		DocumentResponse document = documentService.findById(documentId);
		validateCanViewContext(authentication, document.contextType(), document.contextId());

		return ApiResponse.ok(documentService.findVersions(documentId));
	}

	@PostMapping("/{documentId}/versions/{versionId}/validate")
	public ApiResponse<DocumentVersionResponse> validateVersion(
			Authentication authentication,
			@PathVariable Long documentId,
			@PathVariable Long versionId) {
		DocumentResponse document = documentService.findById(documentId);
		validateCanManageContext(authentication, document.contextType(), document.contextId());

		return ApiResponse.ok("Document version validated", documentService.validateVersion(documentId, versionId));
	}

	@PostMapping("/{documentId}/versions/{versionId}/extract")
	public ApiResponse<DocumentVersionResponse> extractVersionText(
			Authentication authentication,
			@PathVariable Long documentId,
			@PathVariable Long versionId) {
		DocumentResponse document = documentService.findById(documentId);
		validateCanManageContext(authentication, document.contextType(), document.contextId());

		return ApiResponse.ok("Document version text extracted", documentService.extractVersionText(documentId, versionId));
	}

	@PostMapping("/{documentId}/validate-current")
	public ApiResponse<DocumentResponse> validateCurrentVersion(Authentication authentication, @PathVariable Long documentId) {
		DocumentResponse document = documentService.findById(documentId);
		validateCanManageContext(authentication, document.contextType(), document.contextId());

		return ApiResponse.ok("Current document version validated", documentService.validateCurrentVersion(documentId));
	}

	@GetMapping("/context/{contextType}/{contextId}")
	public ApiResponse<List<DocumentSummaryResponse>> findByContext(
			Authentication authentication,
			@PathVariable DocumentContextType contextType,
			@PathVariable Long contextId) {
		validateCanViewContext(authentication, contextType, contextId);

		return ApiResponse.ok(documentService.findByContext(contextType, contextId));
	}

	@DeleteMapping("/{documentId}")
	public ApiResponse<Void> delete(Authentication authentication, @PathVariable Long documentId) {
		DocumentResponse document = documentService.findById(documentId);
		validateCanManageContext(authentication, document.contextType(), document.contextId());
		documentService.delete(documentId);

		return ApiResponse.ok("Document deleted", null);
	}

	private void validateCanViewContext(Authentication authentication, DocumentContextType contextType, Long contextId) {
		if (contextType == DocumentContextType.PROJECT_PROPOSAL) {
			projectProposalService.validateCanViewProposalDocuments(authentication.getName(), contextId);
		}
	}

	private void validateCanManageContext(Authentication authentication, DocumentContextType contextType, Long contextId) {
		if (contextType == DocumentContextType.PROJECT_PROPOSAL) {
			projectProposalService.validateCanManageProposalDocuments(authentication.getName(), contextId);
		}
	}
}
