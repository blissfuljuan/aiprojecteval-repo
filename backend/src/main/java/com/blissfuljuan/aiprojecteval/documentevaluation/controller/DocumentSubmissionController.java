package com.blissfuljuan.aiprojecteval.documentevaluation.controller;

import com.blissfuljuan.aiprojecteval.common.response.ApiResponse;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.request.CreateDocumentSubmissionDraftRequest;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.request.CreateDocumentSubmissionRequest;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.request.DocumentSubmissionFileMetadataRequest;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.request.UpdateDocumentSubmissionDraftRequest;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.response.DocumentSubmissionFileResource;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.response.DocumentSubmissionFileResponse;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.response.DocumentSubmissionResponse;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.response.DocumentSubmissionSummaryResponse;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.response.FileUploadResponse;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.response.MultipleFileUploadResponse;
import com.blissfuljuan.aiprojecteval.documentevaluation.enums.DocumentSubmissionStatus;
import com.blissfuljuan.aiprojecteval.documentevaluation.service.DocumentSubmissionFileService;
import com.blissfuljuan.aiprojecteval.documentevaluation.service.DocumentSubmissionService;
import jakarta.validation.Valid;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/document-evaluation")
public class DocumentSubmissionController {

	private final DocumentSubmissionService submissionService;
	private final DocumentSubmissionFileService fileService;

	public DocumentSubmissionController(
			DocumentSubmissionService submissionService,
			DocumentSubmissionFileService fileService) {
		this.submissionService = submissionService;
		this.fileService = fileService;
	}

	@PostMapping("/submissions/draft")
	@PreAuthorize("isAuthenticated()")
	public ApiResponse<DocumentSubmissionResponse> createDraftSubmission(
			Authentication authentication,
			@Valid @RequestBody CreateDocumentSubmissionDraftRequest request) {
		return ApiResponse.ok("Draft submission created",
				submissionService.createDraftSubmission(authentication.getName(), request));
	}

	@PostMapping("/submissions")
	@PreAuthorize("isAuthenticated()")
	public ApiResponse<DocumentSubmissionResponse> createSubmission(
			Authentication authentication,
			@Valid @RequestBody CreateDocumentSubmissionRequest request) {
		return ApiResponse.ok("Document submission created",
				submissionService.createSubmission(authentication.getName(), request));
	}

	@PutMapping("/submissions/{submissionId}/draft")
	@PreAuthorize("isAuthenticated()")
	public ApiResponse<DocumentSubmissionResponse> updateDraftSubmission(
			Authentication authentication,
			@PathVariable Long submissionId,
			@Valid @RequestBody UpdateDocumentSubmissionDraftRequest request) {
		return ApiResponse.ok("Draft submission updated",
				submissionService.updateDraftSubmission(authentication.getName(), submissionId, request));
	}

	@PatchMapping("/submissions/{submissionId}/submit")
	@PreAuthorize("isAuthenticated()")
	public ApiResponse<DocumentSubmissionResponse> submitDraftSubmission(
			Authentication authentication,
			@PathVariable Long submissionId) {
		return ApiResponse.ok("Draft submission submitted",
				submissionService.submitDraftSubmission(authentication.getName(), submissionId));
	}

	@GetMapping("/submissions/my")
	@PreAuthorize("isAuthenticated()")
	public ApiResponse<List<DocumentSubmissionSummaryResponse>> getMySubmissions(
			Authentication authentication,
			@RequestParam(required = false) Long assignmentId,
			@RequestParam(required = false) Long documentRequirementId,
			@RequestParam(required = false) DocumentSubmissionStatus status,
			@RequestParam(required = false) Long projectId,
			@RequestParam(required = false) Long courseClassId) {
		return ApiResponse.ok(submissionService.getMySubmissions(
				authentication.getName(),
				assignmentId,
				documentRequirementId,
				status,
				projectId,
				courseClassId));
	}

	@GetMapping("/submissions/{submissionId}")
	@PreAuthorize("isAuthenticated()")
	public ApiResponse<DocumentSubmissionResponse> getSubmissionById(
			Authentication authentication,
			@PathVariable Long submissionId) {
		return ApiResponse.ok(submissionService.getSubmissionById(authentication.getName(), submissionId));
	}

	@GetMapping("/requirement-set-assignments/{assignmentId}/submissions")
	@PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
	public ApiResponse<List<DocumentSubmissionSummaryResponse>> getSubmissionsByAssignment(
			Authentication authentication,
			@PathVariable Long assignmentId,
			@RequestParam(required = false) Long documentRequirementId,
			@RequestParam(required = false) DocumentSubmissionStatus status,
			@RequestParam(required = false) Long submittedById,
			@RequestParam(required = false) Long projectId,
			@RequestParam(required = false) Long courseClassId) {
		return ApiResponse.ok(submissionService.getSubmissionsByAssignment(
				authentication.getName(),
				assignmentId,
				documentRequirementId,
				status,
				submittedById,
				projectId,
				courseClassId));
	}

	@GetMapping("/projects/{projectId}/submissions")
	@PreAuthorize("isAuthenticated()")
	public ApiResponse<List<DocumentSubmissionSummaryResponse>> getSubmissionsByProject(
			Authentication authentication,
			@PathVariable Long projectId,
			@RequestParam(required = false) Long documentRequirementId,
			@RequestParam(required = false) DocumentSubmissionStatus status,
			@RequestParam(required = false) Long submittedById) {
		return ApiResponse.ok(submissionService.getSubmissionsByProject(
				authentication.getName(),
				projectId,
				documentRequirementId,
				status,
				submittedById));
	}

	@GetMapping("/classes/{courseClassId}/submissions")
	@PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
	public ApiResponse<List<DocumentSubmissionSummaryResponse>> getSubmissionsByClass(
			Authentication authentication,
			@PathVariable Long courseClassId,
			@RequestParam(required = false) Long documentRequirementId,
			@RequestParam(required = false) DocumentSubmissionStatus status,
			@RequestParam(required = false) Long submittedById) {
		return ApiResponse.ok(submissionService.getSubmissionsByClass(
				authentication.getName(),
				courseClassId,
				documentRequirementId,
				status,
				submittedById));
	}

	@PatchMapping("/submissions/{submissionId}/archive")
	@PreAuthorize("isAuthenticated()")
	public ApiResponse<DocumentSubmissionResponse> archiveSubmission(
			Authentication authentication,
			@PathVariable Long submissionId) {
		return ApiResponse.ok("Document submission archived",
				submissionService.archiveSubmission(authentication.getName(), submissionId));
	}

	@PostMapping("/submissions/{submissionId}/files")
	@PreAuthorize("isAuthenticated()")
	public ApiResponse<DocumentSubmissionResponse> addFileMetadataToDraftSubmission(
			Authentication authentication,
			@PathVariable Long submissionId,
			@Valid @RequestBody DocumentSubmissionFileMetadataRequest request) {
		return ApiResponse.ok("Submission file metadata added",
				submissionService.addFileMetadataToDraftSubmission(authentication.getName(), submissionId, request));
	}

	@PostMapping(
			value = "/submissions/{submissionId}/files/upload",
			consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@PreAuthorize("isAuthenticated()")
	public ApiResponse<FileUploadResponse> uploadFileToSubmission(
			Authentication authentication,
			@PathVariable Long submissionId,
			@RequestParam("file") MultipartFile file,
			@RequestParam(required = false) Long replaceFileId,
			@RequestParam(required = false) String notes) {
		if (replaceFileId != null) {
			return ApiResponse.ok("Submission file replaced",
					fileService.replaceFile(replaceFileId, file, authentication.getName()));
		}
		return ApiResponse.ok("Submission file uploaded",
				fileService.uploadFileToSubmission(authentication.getName(), submissionId, file, notes));
	}

	@PostMapping(
			value = "/submissions/{submissionId}/files/upload-multiple",
			consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@PreAuthorize("isAuthenticated()")
	public ApiResponse<MultipleFileUploadResponse> uploadMultipleFilesToSubmission(
			Authentication authentication,
			@PathVariable Long submissionId,
			@RequestParam("files") List<MultipartFile> files) {
		return ApiResponse.ok("Submission files uploaded",
				fileService.uploadMultipleFilesToSubmission(authentication.getName(), submissionId, files));
	}

	@GetMapping("/submissions/{submissionId}/files")
	@PreAuthorize("isAuthenticated()")
	public ApiResponse<List<DocumentSubmissionFileResponse>> getFilesBySubmission(
			Authentication authentication,
			@PathVariable Long submissionId,
			@RequestParam(defaultValue = "false") boolean includeInactive) {
		return ApiResponse.ok(fileService.getFilesBySubmission(
				submissionId,
				includeInactive,
				authentication.getName()));
	}

	@GetMapping("/submissions/files/{fileId}/download")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<Resource> downloadSubmissionFile(
			Authentication authentication,
			@PathVariable Long fileId) {
		DocumentSubmissionFileResource fileResource = fileService.downloadFile(fileId, authentication.getName());
		return resourceResponse(fileResource, false);
	}

	@GetMapping("/submissions/files/{fileId}/view")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<Resource> viewSubmissionFile(
			Authentication authentication,
			@PathVariable Long fileId) {
		DocumentSubmissionFileResource fileResource = fileService.viewFile(fileId, authentication.getName());
		return resourceResponse(fileResource, true);
	}

	@DeleteMapping("/submissions/files/{fileId}")
	@PreAuthorize("isAuthenticated()")
	public ApiResponse<DocumentSubmissionFileResponse> removeFileMetadataFromDraftSubmission(
			Authentication authentication,
			@PathVariable Long fileId) {
		return ApiResponse.ok("Submission file removed",
				fileService.removeFile(fileId, authentication.getName()));
	}

	@PostMapping(
			value = "/submissions/files/{fileId}/replace",
			consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@PreAuthorize("isAuthenticated()")
	public ApiResponse<FileUploadResponse> replaceSubmissionFile(
			Authentication authentication,
			@PathVariable Long fileId,
			@RequestParam("file") MultipartFile file) {
		return ApiResponse.ok("Submission file replaced",
				fileService.replaceFile(fileId, file, authentication.getName()));
	}

	private ResponseEntity<Resource> resourceResponse(
			DocumentSubmissionFileResource fileResource,
			boolean inline) {
		MediaType mediaType = parseMediaType(fileResource.contentType());
		ContentDisposition contentDisposition = inline
				? ContentDisposition.inline()
						.filename(fileResource.originalFileName(), StandardCharsets.UTF_8)
						.build()
				: ContentDisposition.attachment()
						.filename(fileResource.originalFileName(), StandardCharsets.UTF_8)
						.build();

		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
				.contentType(mediaType)
				.body(fileResource.resource());
	}

	private MediaType parseMediaType(String contentType) {
		if (contentType == null || contentType.isBlank()) {
			return MediaType.APPLICATION_OCTET_STREAM;
		}
		return MediaType.parseMediaType(contentType);
	}
}
