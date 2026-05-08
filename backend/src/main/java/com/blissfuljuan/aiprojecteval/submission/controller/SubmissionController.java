package com.blissfuljuan.aiprojecteval.submission.controller;

import com.blissfuljuan.aiprojecteval.common.response.ApiResponse;
import com.blissfuljuan.aiprojecteval.submission.dto.request.CreateSubmissionDraftRequest;
import com.blissfuljuan.aiprojecteval.submission.dto.request.CreateSubmissionRequest;
import com.blissfuljuan.aiprojecteval.submission.dto.request.SubmissionFileMetadataRequest;
import com.blissfuljuan.aiprojecteval.submission.dto.request.UpdateSubmissionDraftRequest;
import com.blissfuljuan.aiprojecteval.submission.dto.response.SubmissionFileResource;
import com.blissfuljuan.aiprojecteval.submission.dto.response.SubmissionFileResponse;
import com.blissfuljuan.aiprojecteval.submission.dto.response.SubmissionResponse;
import com.blissfuljuan.aiprojecteval.submission.dto.response.SubmissionSummaryResponse;
import com.blissfuljuan.aiprojecteval.submission.dto.response.FileUploadResponse;
import com.blissfuljuan.aiprojecteval.submission.dto.response.MultipleFileUploadResponse;
import com.blissfuljuan.aiprojecteval.submission.enums.SubmissionStatus;
import com.blissfuljuan.aiprojecteval.submission.service.SubmissionFileService;
import com.blissfuljuan.aiprojecteval.submission.service.SubmissionService;
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
@RequestMapping
public class SubmissionController {

	private final SubmissionService submissionService;
	private final SubmissionFileService fileService;

	public SubmissionController(
			SubmissionService submissionService,
			SubmissionFileService fileService) {
		this.submissionService = submissionService;
		this.fileService = fileService;
	}

	@PostMapping("/api/submissions/draft")
	@PreAuthorize("isAuthenticated()")
	public ApiResponse<SubmissionResponse> createDraftSubmission(
			Authentication authentication,
			@Valid @RequestBody CreateSubmissionDraftRequest request) {
		return ApiResponse.ok("Draft submission created",
				submissionService.createDraftSubmission(authentication.getName(), request));
	}

	@PostMapping("/api/submissions")
	@PreAuthorize("isAuthenticated()")
	public ApiResponse<SubmissionResponse> createSubmission(
			Authentication authentication,
			@Valid @RequestBody CreateSubmissionRequest request) {
		return ApiResponse.ok("Document submission created",
				submissionService.createSubmission(authentication.getName(), request));
	}

	@PutMapping("/api/submissions/{submissionId}/draft")
	@PreAuthorize("isAuthenticated()")
	public ApiResponse<SubmissionResponse> updateDraftSubmission(
			Authentication authentication,
			@PathVariable Long submissionId,
			@Valid @RequestBody UpdateSubmissionDraftRequest request) {
		return ApiResponse.ok("Draft submission updated",
				submissionService.updateDraftSubmission(authentication.getName(), submissionId, request));
	}

	@PatchMapping("/api/submissions/{submissionId}/submit")
	@PreAuthorize("isAuthenticated()")
	public ApiResponse<SubmissionResponse> submitDraftSubmission(
			Authentication authentication,
			@PathVariable Long submissionId) {
		return ApiResponse.ok("Draft submission submitted",
				submissionService.submitDraftSubmission(authentication.getName(), submissionId));
	}

	@GetMapping("/api/submissions/my")
	@PreAuthorize("isAuthenticated()")
	public ApiResponse<List<SubmissionSummaryResponse>> getMySubmissions(
			Authentication authentication,
			@RequestParam(required = false) Long assignmentId,
			@RequestParam(required = false) Long requirementId,
			@RequestParam(required = false) SubmissionStatus status,
			@RequestParam(required = false) Long projectId,
			@RequestParam(required = false) Long courseClassId) {
		return ApiResponse.ok(submissionService.getMySubmissions(
				authentication.getName(),
				assignmentId,
				requirementId,
				status,
				projectId,
				courseClassId));
	}

	@GetMapping("/api/submissions/{submissionId}")
	@PreAuthorize("isAuthenticated()")
	public ApiResponse<SubmissionResponse> getSubmissionById(
			Authentication authentication,
			@PathVariable Long submissionId) {
		return ApiResponse.ok(submissionService.getSubmissionById(authentication.getName(), submissionId));
	}

	@GetMapping("/api/submissions")
	@PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
	public ApiResponse<List<SubmissionSummaryResponse>> getSubmissionsByAssignment(
			Authentication authentication,
			@RequestParam Long assignmentId,
			@RequestParam(required = false) Long requirementId,
			@RequestParam(required = false) SubmissionStatus status,
			@RequestParam(required = false) Long submittedById,
			@RequestParam(required = false) Long projectId,
			@RequestParam(required = false) Long courseClassId) {
		return ApiResponse.ok(submissionService.getSubmissionsByAssignment(
				authentication.getName(),
				assignmentId,
				requirementId,
				status,
				submittedById,
				projectId,
				courseClassId));
	}

	@GetMapping("/api/submissions/by-project/{projectId}")
	@PreAuthorize("isAuthenticated()")
	public ApiResponse<List<SubmissionSummaryResponse>> getSubmissionsByProject(
			Authentication authentication,
			@PathVariable Long projectId,
			@RequestParam(required = false) Long requirementId,
			@RequestParam(required = false) SubmissionStatus status,
			@RequestParam(required = false) Long submittedById) {
		return ApiResponse.ok(submissionService.getSubmissionsByProject(
				authentication.getName(),
				projectId,
				requirementId,
				status,
				submittedById));
	}

	@GetMapping("/api/submissions/by-class/{courseClassId}")
	@PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
	public ApiResponse<List<SubmissionSummaryResponse>> getSubmissionsByClass(
			Authentication authentication,
			@PathVariable Long courseClassId,
			@RequestParam(required = false) Long requirementId,
			@RequestParam(required = false) SubmissionStatus status,
			@RequestParam(required = false) Long submittedById) {
		return ApiResponse.ok(submissionService.getSubmissionsByClass(
				authentication.getName(),
				courseClassId,
				requirementId,
				status,
				submittedById));
	}

	@PatchMapping("/api/submissions/{submissionId}/archive")
	@PreAuthorize("isAuthenticated()")
	public ApiResponse<SubmissionResponse> archiveSubmission(
			Authentication authentication,
			@PathVariable Long submissionId) {
		return ApiResponse.ok("Document submission archived",
				submissionService.archiveSubmission(authentication.getName(), submissionId));
	}

	@PostMapping("/api/submissions/{submissionId}/files")
	@PreAuthorize("isAuthenticated()")
	public ApiResponse<SubmissionResponse> addFileMetadataToDraftSubmission(
			Authentication authentication,
			@PathVariable Long submissionId,
			@Valid @RequestBody SubmissionFileMetadataRequest request) {
		return ApiResponse.ok("Submission file metadata added",
				submissionService.addFileMetadataToDraftSubmission(authentication.getName(), submissionId, request));
	}

	@PostMapping(
			value = "/api/submissions/{submissionId}/files/upload",
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
			value = "/api/submissions/{submissionId}/files/upload-multiple",
			consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@PreAuthorize("isAuthenticated()")
	public ApiResponse<MultipleFileUploadResponse> uploadMultipleFilesToSubmission(
			Authentication authentication,
			@PathVariable Long submissionId,
			@RequestParam("files") List<MultipartFile> files) {
		return ApiResponse.ok("Submission files uploaded",
				fileService.uploadMultipleFilesToSubmission(authentication.getName(), submissionId, files));
	}

	@GetMapping("/api/submissions/{submissionId}/files")
	@PreAuthorize("isAuthenticated()")
	public ApiResponse<List<SubmissionFileResponse>> getFilesBySubmission(
			Authentication authentication,
			@PathVariable Long submissionId,
			@RequestParam(defaultValue = "false") boolean includeInactive) {
		return ApiResponse.ok(fileService.getFilesBySubmission(
				submissionId,
				includeInactive,
				authentication.getName()));
	}

	@GetMapping("/api/submission-files/{fileId}/download")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<Resource> downloadSubmissionFile(
			Authentication authentication,
			@PathVariable Long fileId) {
		SubmissionFileResource fileResource = fileService.downloadFile(fileId, authentication.getName());
		return resourceResponse(fileResource, false);
	}

	@GetMapping("/api/submission-files/{fileId}/view")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<Resource> viewSubmissionFile(
			Authentication authentication,
			@PathVariable Long fileId) {
		SubmissionFileResource fileResource = fileService.viewFile(fileId, authentication.getName());
		return resourceResponse(fileResource, true);
	}

	@DeleteMapping("/api/submission-files/{fileId}")
	@PreAuthorize("isAuthenticated()")
	public ApiResponse<SubmissionFileResponse> removeFileMetadataFromDraftSubmission(
			Authentication authentication,
			@PathVariable Long fileId) {
		return ApiResponse.ok("Submission file removed",
				fileService.removeFile(fileId, authentication.getName()));
	}

	@PostMapping(
			value = "/api/submission-files/{fileId}/replace",
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
			SubmissionFileResource fileResource,
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
