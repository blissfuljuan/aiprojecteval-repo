package com.blissfuljuan.aiprojecteval.submission.service;

import com.blissfuljuan.aiprojecteval.common.exception.BadRequestException;
import com.blissfuljuan.aiprojecteval.common.exception.ResourceNotFoundException;
import com.blissfuljuan.aiprojecteval.submission.dto.response.SubmissionFileResource;
import com.blissfuljuan.aiprojecteval.submission.dto.response.SubmissionFileResponse;
import com.blissfuljuan.aiprojecteval.submission.dto.response.FileUploadResponse;
import com.blissfuljuan.aiprojecteval.submission.dto.response.MultipleFileUploadResponse;
import com.blissfuljuan.aiprojecteval.submission.enums.SubmissionFileStatus;
import com.blissfuljuan.aiprojecteval.submission.mapper.SubmissionMapper;
import com.blissfuljuan.aiprojecteval.documentevaluation.model.DocumentRequirementSet;
import com.blissfuljuan.aiprojecteval.documentevaluation.model.DocumentRequirementSetAssignment;
import com.blissfuljuan.aiprojecteval.documentevaluation.repository.DocumentRequirementSetAssignmentRepository;
import com.blissfuljuan.aiprojecteval.submission.model.Submission;
import com.blissfuljuan.aiprojecteval.submission.model.SubmissionFile;
import com.blissfuljuan.aiprojecteval.submission.repository.SubmissionFileRepository;
import com.blissfuljuan.aiprojecteval.submission.repository.SubmissionRepository;
import com.blissfuljuan.aiprojecteval.submission.storage.FileStorageService;
import com.blissfuljuan.aiprojecteval.submission.storage.StoredFile;
import com.blissfuljuan.aiprojecteval.identity.model.Role;
import com.blissfuljuan.aiprojecteval.identity.model.User;
import com.blissfuljuan.aiprojecteval.identity.service.IdentityQueryService;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import org.springframework.core.io.Resource;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
class SubmissionFileServiceImpl implements SubmissionFileService {

	private static final Set<SubmissionFileStatus> ACTIVE_STATUSES =
			Set.of(SubmissionFileStatus.UPLOADED, SubmissionFileStatus.PENDING_UPLOAD);

	private static final Set<String> INLINE_CONTENT_TYPES =
			Set.of("application/pdf", "image/png", "image/jpeg", "text/plain");

	private final SubmissionRepository submissionRepository;
	private final SubmissionFileRepository fileRepository;
	private final IdentityQueryService identityQueryService;
	private final DocumentRequirementSetAssignmentRepository assignmentRepository;
	private final FileStorageService storageService;
	private final SubmissionFileValidator validator;

	SubmissionFileServiceImpl(
			SubmissionRepository submissionRepository,
			SubmissionFileRepository fileRepository,
			IdentityQueryService identityQueryService,
			DocumentRequirementSetAssignmentRepository assignmentRepository,
			FileStorageService storageService,
			SubmissionFileValidator validator) {
		this.submissionRepository = submissionRepository;
		this.fileRepository = fileRepository;
		this.identityQueryService = identityQueryService;
		this.assignmentRepository = assignmentRepository;
		this.storageService = storageService;
		this.validator = validator;
	}

	@Override
	@Transactional
	public FileUploadResponse uploadFileToSubmission(
			String currentUserEmail,
			Long submissionId,
			MultipartFile file,
			String notes) {
		User currentUser = findUserByEmail(currentUserEmail);
		Submission submission = findSubmission(submissionId);
		checkCanUpdateDraft(currentUser, submission);
		validator.validateUpload(submission, file, 1);

		SubmissionFile savedFile = storeAndSaveFile(file, submission);
		return new FileUploadResponse(
				SubmissionMapper.toFileResponse(savedFile),
				"File uploaded successfully");
	}

	@Override
	@Transactional
	public MultipleFileUploadResponse uploadMultipleFilesToSubmission(
			String currentUserEmail,
			Long submissionId,
			List<MultipartFile> files) {
		if (files == null || files.isEmpty()) {
			throw new BadRequestException("At least one file is required");
		}

		User currentUser = findUserByEmail(currentUserEmail);
		Submission submission = findSubmission(submissionId);
		checkCanUpdateDraft(currentUser, submission);
		files.forEach(file -> validator.validateUpload(submission, file, files.size()));

		List<SubmissionFileResponse> uploadedFiles = files.stream()
				.map(file -> storeAndSaveFile(file, submission))
				.map(SubmissionMapper::toFileResponse)
				.toList();

		return new MultipleFileUploadResponse(
				submission.getId(),
				uploadedFiles,
				uploadedFiles.size(),
				"Files uploaded successfully");
	}

	@Override
	@Transactional(readOnly = true)
	public SubmissionFileResource downloadFile(Long fileId, String currentUserEmail) {
		User currentUser = findUserByEmail(currentUserEmail);
		SubmissionFile file = findFile(fileId);
		checkCanViewSubmission(currentUser, file.getSubmission());
		validator.validateReadableUploadedFile(file);
		Resource resource = storageService.loadAsResource(file.getStoragePath());
		return new SubmissionFileResource(resource, file.getOriginalFileName(), file.getContentType());
	}

	@Override
	@Transactional(readOnly = true)
	public SubmissionFileResource viewFile(Long fileId, String currentUserEmail) {
		SubmissionFileResource fileResource = downloadFile(fileId, currentUserEmail);
		String contentType = fileResource.contentType();
		if (contentType == null || !INLINE_CONTENT_TYPES.contains(contentType.toLowerCase())) {
			throw new BadRequestException("This file type is not supported for inline viewing");
		}
		return fileResource;
	}

	@Override
	@Transactional
	public SubmissionFileResponse removeFile(Long fileId, String currentUserEmail) {
		User currentUser = findUserByEmail(currentUserEmail);
		SubmissionFile file = findFile(fileId);
		checkCanUpdateDraft(currentUser, file.getSubmission());
		file.setFileStatus(SubmissionFileStatus.REMOVED);
		// TODO: add a retention policy before deleting physical files during soft removal.
		touchSubmission(file.getSubmission());
		return SubmissionMapper.toFileResponse(fileRepository.save(file));
	}

	@Override
	@Transactional
	public FileUploadResponse replaceFile(Long fileId, MultipartFile file, String currentUserEmail) {
		User currentUser = findUserByEmail(currentUserEmail);
		SubmissionFile oldFile = findFile(fileId);
		Submission submission = oldFile.getSubmission();
		checkCanUpdateDraft(currentUser, submission);
		validator.validateUpload(submission, file, 1);

		oldFile.setFileStatus(SubmissionFileStatus.REPLACED);
		fileRepository.save(oldFile);
		SubmissionFile savedFile = storeAndSaveFile(file, submission);

		return new FileUploadResponse(
				SubmissionMapper.toFileResponse(savedFile),
				"File replaced successfully");
	}

	@Override
	@Transactional(readOnly = true)
	public List<SubmissionFileResponse> getFilesBySubmission(
			Long submissionId,
			boolean includeInactive,
			String currentUserEmail) {
		User currentUser = findUserByEmail(currentUserEmail);
		Submission submission = findSubmission(submissionId);
		checkCanViewSubmission(currentUser, submission);

		List<SubmissionFile> files = includeInactive
				? fileRepository.findBySubmissionIdOrderByCreatedAtAsc(submissionId)
				: fileRepository.findBySubmissionIdAndFileStatusInOrderByCreatedAtAsc(submissionId, ACTIVE_STATUSES);
		return files.stream()
				.sorted(Comparator.comparing(file -> file.getId() == null ? 0L : file.getId()))
				.map(SubmissionMapper::toFileResponse)
				.toList();
	}

	private SubmissionFile storeAndSaveFile(MultipartFile multipartFile, Submission submission) {
		StoredFile storedFile = storageService.store(multipartFile, submission);

		SubmissionFile file = new SubmissionFile();
		file.setSubmission(submission);
		file.setOriginalFileName(storedFile.originalFileName());
		file.setStoredFileName(storedFile.storedFileName());
		file.setStoragePath(storedFile.storagePath());
		file.setContentType(storedFile.contentType());
		file.setFileSize(storedFile.fileSize());
		file.setFileExtension(storedFile.fileExtension());
		file.setChecksum(storedFile.checksum());
		file.setFileStatus(SubmissionFileStatus.UPLOADED);
		file.setUploadedAt(LocalDateTime.now());

		SubmissionFile savedFile = fileRepository.save(file);
		savedFile.setFileUrl("/api/submission-files/" + savedFile.getId() + "/download");
		touchSubmission(submission);
		return fileRepository.save(savedFile);
	}

	private void touchSubmission(Submission submission) {
		submission.setLastUpdatedAt(LocalDateTime.now());
		submissionRepository.save(submission);
	}

	private void checkCanUpdateDraft(User currentUser, Submission submission) {
		validator.validateEditableSubmission(submission);
		if (isOwner(currentUser, submission) || canManageAssignment(currentUser, findAssignment(submission.getAssignmentId()))) {
			return;
		}
		throw new AccessDeniedException("Access denied");
	}

	private void checkCanViewSubmission(User currentUser, Submission submission) {
		if (isOwner(currentUser, submission)
				|| canManageAssignment(currentUser, findAssignment(submission.getAssignmentId()))
				|| isProjectOwner(currentUser, submission.getProjectId())) {
			return;
		}
		throw new AccessDeniedException("Access denied");
	}

	private boolean isOwner(User currentUser, Submission submission) {
		return submission.getSubmittedBy() != null
				&& currentUser.getId().equals(submission.getSubmittedBy().getId());
	}

	private boolean canManageAssignment(User currentUser, DocumentRequirementSetAssignment assignment) {
		if (assignment == null) {
			return false;
		}
		if (currentUser.getRole() == Role.ADMIN) {
			return true;
		}
		DocumentRequirementSet requirementSet = assignment.getRequirementSet();
		return currentUser.getRole() == Role.INSTRUCTOR
				&& requirementSet != null
				&& requirementSet.getOwnerInstructor() != null
				&& currentUser.getId().equals(requirementSet.getOwnerInstructor().getId());
	}

	private boolean isProjectOwner(User currentUser, Long projectId) {
		DocumentRequirementSetAssignment assignment = findAssignmentByProjectId(projectId);
		return assignment != null
				&& assignment.getProject() != null
				&& currentUser.getId().equals(assignment.getProject().getOwnerUserId());
	}

	private DocumentRequirementSetAssignment findAssignment(Long id) {
		return assignmentRepository.findById(id).orElse(null);
	}

	private DocumentRequirementSetAssignment findAssignmentByProjectId(Long projectId) {
		if (projectId == null) {
			return null;
		}
		return submissionRepository.findByTypeAndProjectIdOrderByCreatedAtDesc(
						com.blissfuljuan.aiprojecteval.submission.enums.SubmissionType.DOCUMENT,
						projectId)
				.stream()
				.findFirst()
				.map(submission -> findAssignment(submission.getAssignmentId()))
				.orElse(null);
	}

	private Submission findSubmission(Long id) {
		return submissionRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Document submission not found"));
	}

	private SubmissionFile findFile(Long id) {
		return fileRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Submission file not found"));
	}

	private User findUserByEmail(String email) {
		return identityQueryService.getUserByEmail(email);
	}
}
