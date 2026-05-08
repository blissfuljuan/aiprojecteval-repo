package com.blissfuljuan.aiprojecteval.documentevaluation.service;

import com.blissfuljuan.aiprojecteval.common.exception.BadRequestException;
import com.blissfuljuan.aiprojecteval.common.exception.ResourceNotFoundException;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.response.DocumentSubmissionFileResource;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.response.DocumentSubmissionFileResponse;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.response.FileUploadResponse;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.response.MultipleFileUploadResponse;
import com.blissfuljuan.aiprojecteval.documentevaluation.enums.DocumentSubmissionFileStatus;
import com.blissfuljuan.aiprojecteval.documentevaluation.mapper.DocumentSubmissionMapper;
import com.blissfuljuan.aiprojecteval.documentevaluation.model.DocumentRequirementSet;
import com.blissfuljuan.aiprojecteval.documentevaluation.model.DocumentRequirementSetAssignment;
import com.blissfuljuan.aiprojecteval.documentevaluation.model.DocumentSubmission;
import com.blissfuljuan.aiprojecteval.documentevaluation.model.DocumentSubmissionFile;
import com.blissfuljuan.aiprojecteval.documentevaluation.repository.DocumentSubmissionFileRepository;
import com.blissfuljuan.aiprojecteval.documentevaluation.repository.DocumentSubmissionRepository;
import com.blissfuljuan.aiprojecteval.documentevaluation.storage.DocumentFileStorageService;
import com.blissfuljuan.aiprojecteval.documentevaluation.storage.StoredDocumentFile;
import com.blissfuljuan.aiprojecteval.identity.model.Role;
import com.blissfuljuan.aiprojecteval.identity.model.User;
import com.blissfuljuan.aiprojecteval.identity.repository.UserRepository;
import com.blissfuljuan.aiprojecteval.project.model.Project;
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
class DocumentSubmissionFileServiceImpl implements DocumentSubmissionFileService {

	private static final Set<DocumentSubmissionFileStatus> ACTIVE_STATUSES =
			Set.of(DocumentSubmissionFileStatus.UPLOADED, DocumentSubmissionFileStatus.PENDING_UPLOAD);

	private static final Set<String> INLINE_CONTENT_TYPES =
			Set.of("application/pdf", "image/png", "image/jpeg", "text/plain");

	private final DocumentSubmissionRepository submissionRepository;
	private final DocumentSubmissionFileRepository fileRepository;
	private final UserRepository userRepository;
	private final DocumentFileStorageService storageService;
	private final DocumentSubmissionFileValidator validator;

	DocumentSubmissionFileServiceImpl(
			DocumentSubmissionRepository submissionRepository,
			DocumentSubmissionFileRepository fileRepository,
			UserRepository userRepository,
			DocumentFileStorageService storageService,
			DocumentSubmissionFileValidator validator) {
		this.submissionRepository = submissionRepository;
		this.fileRepository = fileRepository;
		this.userRepository = userRepository;
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
		DocumentSubmission submission = findSubmission(submissionId);
		checkCanUpdateDraft(currentUser, submission);
		validator.validateUpload(submission, file, 1);

		DocumentSubmissionFile savedFile = storeAndSaveFile(file, submission);
		return new FileUploadResponse(
				DocumentSubmissionMapper.toFileResponse(savedFile),
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
		DocumentSubmission submission = findSubmission(submissionId);
		checkCanUpdateDraft(currentUser, submission);
		files.forEach(file -> validator.validateUpload(submission, file, files.size()));

		List<DocumentSubmissionFileResponse> uploadedFiles = files.stream()
				.map(file -> storeAndSaveFile(file, submission))
				.map(DocumentSubmissionMapper::toFileResponse)
				.toList();

		return new MultipleFileUploadResponse(
				submission.getId(),
				uploadedFiles,
				uploadedFiles.size(),
				"Files uploaded successfully");
	}

	@Override
	@Transactional(readOnly = true)
	public DocumentSubmissionFileResource downloadFile(Long fileId, String currentUserEmail) {
		User currentUser = findUserByEmail(currentUserEmail);
		DocumentSubmissionFile file = findFile(fileId);
		checkCanViewSubmission(currentUser, file.getSubmission());
		validator.validateReadableUploadedFile(file);
		Resource resource = storageService.loadAsResource(file.getStoragePath());
		return new DocumentSubmissionFileResource(resource, file.getOriginalFileName(), file.getContentType());
	}

	@Override
	@Transactional(readOnly = true)
	public DocumentSubmissionFileResource viewFile(Long fileId, String currentUserEmail) {
		DocumentSubmissionFileResource fileResource = downloadFile(fileId, currentUserEmail);
		String contentType = fileResource.contentType();
		if (contentType == null || !INLINE_CONTENT_TYPES.contains(contentType.toLowerCase())) {
			throw new BadRequestException("This file type is not supported for inline viewing");
		}
		return fileResource;
	}

	@Override
	@Transactional
	public DocumentSubmissionFileResponse removeFile(Long fileId, String currentUserEmail) {
		User currentUser = findUserByEmail(currentUserEmail);
		DocumentSubmissionFile file = findFile(fileId);
		checkCanUpdateDraft(currentUser, file.getSubmission());
		file.setFileStatus(DocumentSubmissionFileStatus.REMOVED);
		// TODO: add a retention policy before deleting physical files during soft removal.
		touchSubmission(file.getSubmission());
		return DocumentSubmissionMapper.toFileResponse(fileRepository.save(file));
	}

	@Override
	@Transactional
	public FileUploadResponse replaceFile(Long fileId, MultipartFile file, String currentUserEmail) {
		User currentUser = findUserByEmail(currentUserEmail);
		DocumentSubmissionFile oldFile = findFile(fileId);
		DocumentSubmission submission = oldFile.getSubmission();
		checkCanUpdateDraft(currentUser, submission);
		validator.validateUpload(submission, file, 1);

		oldFile.setFileStatus(DocumentSubmissionFileStatus.REPLACED);
		fileRepository.save(oldFile);
		DocumentSubmissionFile savedFile = storeAndSaveFile(file, submission);

		return new FileUploadResponse(
				DocumentSubmissionMapper.toFileResponse(savedFile),
				"File replaced successfully");
	}

	@Override
	@Transactional(readOnly = true)
	public List<DocumentSubmissionFileResponse> getFilesBySubmission(
			Long submissionId,
			boolean includeInactive,
			String currentUserEmail) {
		User currentUser = findUserByEmail(currentUserEmail);
		DocumentSubmission submission = findSubmission(submissionId);
		checkCanViewSubmission(currentUser, submission);

		List<DocumentSubmissionFile> files = includeInactive
				? fileRepository.findBySubmissionIdOrderByCreatedAtAsc(submissionId)
				: fileRepository.findBySubmissionIdAndFileStatusInOrderByCreatedAtAsc(submissionId, ACTIVE_STATUSES);
		return files.stream()
				.sorted(Comparator.comparing(file -> file.getId() == null ? 0L : file.getId()))
				.map(DocumentSubmissionMapper::toFileResponse)
				.toList();
	}

	private DocumentSubmissionFile storeAndSaveFile(MultipartFile multipartFile, DocumentSubmission submission) {
		StoredDocumentFile storedFile = storageService.store(multipartFile, submission);

		DocumentSubmissionFile file = new DocumentSubmissionFile();
		file.setSubmission(submission);
		file.setOriginalFileName(storedFile.originalFileName());
		file.setStoredFileName(storedFile.storedFileName());
		file.setStoragePath(storedFile.storagePath());
		file.setContentType(storedFile.contentType());
		file.setFileSize(storedFile.fileSize());
		file.setFileExtension(storedFile.fileExtension());
		file.setChecksum(storedFile.checksum());
		file.setFileStatus(DocumentSubmissionFileStatus.UPLOADED);
		file.setUploadedAt(LocalDateTime.now());

		DocumentSubmissionFile savedFile = fileRepository.save(file);
		savedFile.setFileUrl("/api/document-evaluation/submissions/files/" + savedFile.getId() + "/download");
		touchSubmission(submission);
		return fileRepository.save(savedFile);
	}

	private void touchSubmission(DocumentSubmission submission) {
		submission.setLastUpdatedAt(LocalDateTime.now());
		submissionRepository.save(submission);
	}

	private void checkCanUpdateDraft(User currentUser, DocumentSubmission submission) {
		validator.validateEditableSubmission(submission);
		if (isOwner(currentUser, submission) || canManageAssignment(currentUser, submission.getAssignment())) {
			return;
		}
		throw new AccessDeniedException("Access denied");
	}

	private void checkCanViewSubmission(User currentUser, DocumentSubmission submission) {
		if (isOwner(currentUser, submission)
				|| canManageAssignment(currentUser, submission.getAssignment())
				|| isProjectOwner(currentUser, submission.getProject())) {
			return;
		}
		throw new AccessDeniedException("Access denied");
	}

	private boolean isOwner(User currentUser, DocumentSubmission submission) {
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

	private boolean isProjectOwner(User currentUser, Project project) {
		return project != null && currentUser.getId().equals(project.getOwnerUserId());
	}

	private DocumentSubmission findSubmission(Long id) {
		return submissionRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Document submission not found"));
	}

	private DocumentSubmissionFile findFile(Long id) {
		return fileRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Submission file not found"));
	}

	private User findUserByEmail(String email) {
		return userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));
	}
}
