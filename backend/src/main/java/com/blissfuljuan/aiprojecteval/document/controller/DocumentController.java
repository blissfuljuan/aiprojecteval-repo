package com.blissfuljuan.aiprojecteval.document.controller;

import com.blissfuljuan.aiprojecteval.common.response.ApiResponse;
import com.blissfuljuan.aiprojecteval.document.dto.DocumentLinkSubmitRequest;
import com.blissfuljuan.aiprojecteval.document.dto.DocumentResponse;
import com.blissfuljuan.aiprojecteval.document.dto.DocumentSummaryResponse;
import com.blissfuljuan.aiprojecteval.document.dto.DocumentVersionResponse;
import com.blissfuljuan.aiprojecteval.document.model.DocumentContextType;
import com.blissfuljuan.aiprojecteval.document.service.DocumentService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
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

	public DocumentController(DocumentService documentService) {
		this.documentService = documentService;
	}

	@PostMapping("/link")
	public ApiResponse<DocumentResponse> submitExternalLink(
			@Valid @RequestBody DocumentLinkSubmitRequest request) {
		return ApiResponse.ok("Document link submitted", documentService.submitExternalLink(request));
	}

	@GetMapping("/{documentId}")
	public ApiResponse<DocumentResponse> findById(@PathVariable Long documentId) {
		return ApiResponse.ok(documentService.findById(documentId));
	}

	@GetMapping("/{documentId}/versions")
	public ApiResponse<List<DocumentVersionResponse>> findVersions(@PathVariable Long documentId) {
		return ApiResponse.ok(documentService.findVersions(documentId));
	}

	@PostMapping("/{documentId}/versions/{versionId}/validate")
	public ApiResponse<DocumentVersionResponse> validateVersion(
			@PathVariable Long documentId,
			@PathVariable Long versionId) {
		return ApiResponse.ok("Document version validated", documentService.validateVersion(documentId, versionId));
	}

	@PostMapping("/{documentId}/validate-current")
	public ApiResponse<DocumentResponse> validateCurrentVersion(@PathVariable Long documentId) {
		return ApiResponse.ok("Current document version validated", documentService.validateCurrentVersion(documentId));
	}

	@GetMapping("/context/{contextType}/{contextId}")
	public ApiResponse<List<DocumentSummaryResponse>> findByContext(
			@PathVariable DocumentContextType contextType,
			@PathVariable Long contextId) {
		return ApiResponse.ok(documentService.findByContext(contextType, contextId));
	}

	@DeleteMapping("/{documentId}")
	public ApiResponse<Void> delete(@PathVariable Long documentId) {
		documentService.delete(documentId);

		return ApiResponse.ok("Document deleted", null);
	}
}
