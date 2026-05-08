package com.blissfuljuan.aiprojecteval.submission.service;

import com.blissfuljuan.aiprojecteval.common.exception.BadRequestException;
import com.blissfuljuan.aiprojecteval.documentevaluation.enums.AllowedFileType;
import com.blissfuljuan.aiprojecteval.submission.enums.SubmissionFileStatus;
import com.blissfuljuan.aiprojecteval.submission.enums.SubmissionStatus;
import com.blissfuljuan.aiprojecteval.documentevaluation.model.DocumentRequirement;
import com.blissfuljuan.aiprojecteval.documentevaluation.repository.DocumentRequirementRepository;
import com.blissfuljuan.aiprojecteval.submission.config.SubmissionStorageProperties;
import com.blissfuljuan.aiprojecteval.submission.model.Submission;
import com.blissfuljuan.aiprojecteval.submission.repository.SubmissionFileRepository;
import com.blissfuljuan.aiprojecteval.submission.util.FileNameSanitizer;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
class SubmissionFileValidator {

	private static final List<SubmissionFileStatus> ACTIVE_FILE_STATUSES =
			List.of(SubmissionFileStatus.UPLOADED, SubmissionFileStatus.PENDING_UPLOAD);

	private final SubmissionStorageProperties storageProperties;
	private final SubmissionFileRepository fileRepository;
	private final DocumentRequirementRepository requirementRepository;

	SubmissionFileValidator(
			SubmissionStorageProperties storageProperties,
			SubmissionFileRepository fileRepository,
			DocumentRequirementRepository requirementRepository) {
		this.storageProperties = storageProperties;
		this.fileRepository = fileRepository;
		this.requirementRepository = requirementRepository;
	}

	void validateUpload(Submission submission, MultipartFile file, int filesToUpload) {
		validateEditableSubmission(submission);
		validateFile(file, findRequirement(submission.getRequirementId()));
		validateMaxFileCount(submission, filesToUpload);
	}

	void validateEditableSubmission(Submission submission) {
		if (submission.getStatus() == SubmissionStatus.ARCHIVED) {
			throw new BadRequestException("Archived submissions are read-only");
		}
		if (submission.getStatus() != SubmissionStatus.DRAFT) {
			throw new BadRequestException("Only draft submissions can accept file changes");
		}
	}

	void validateReadableUploadedFile(com.blissfuljuan.aiprojecteval.submission.model.SubmissionFile file) {
		if (file.getFileStatus() != SubmissionFileStatus.UPLOADED) {
			throw new BadRequestException("Only uploaded files can be accessed");
		}
		if (file.getStoragePath() == null || file.getStoragePath().isBlank()) {
			throw new BadRequestException("Stored file path is missing");
		}
	}

	private void validateFile(MultipartFile file, DocumentRequirement requirement) {
		if (file == null || file.isEmpty()) {
			throw new BadRequestException("Uploaded file must not be empty");
		}
		if (file.getSize() > storageProperties.getMaxFileSize()) {
			throw new BadRequestException("Uploaded file exceeds the maximum allowed size");
		}

		String originalFileName = file.getOriginalFilename();
		if (originalFileName == null || originalFileName.isBlank()) {
			throw new BadRequestException("Uploaded file must have an original filename");
		}
		if (originalFileName.contains("..") || originalFileName.contains("/") || originalFileName.contains("\\")) {
			throw new BadRequestException("Uploaded filename is invalid");
		}

		String extension = FileNameSanitizer.extension(originalFileName);
		Set<AllowedFileType> allowedFileTypes = requirement == null ? Set.of() : requirement.getAllowedFileTypes();
		if (allowedFileTypes != null && !allowedFileTypes.isEmpty()) {
			if (extension.isBlank()) {
				throw new BadRequestException("Uploaded file must have an allowed extension");
			}
			boolean allowed = allowedFileTypes.stream()
					.anyMatch(fileType -> fileType.name().equalsIgnoreCase(extension));
			if (!allowed) {
				throw new BadRequestException("File type is not allowed for this document requirement");
			}
		}

		// TODO: add deeper MIME sniffing and requirement-specific file size limits when those fields exist.
	}

	private void validateMaxFileCount(Submission submission, int filesToUpload) {
		long activeFileCount = fileRepository.countBySubmissionIdAndFileStatusIn(
				submission.getId(),
				ACTIVE_FILE_STATUSES);
		// TODO: compare activeFileCount + filesToUpload against a per-requirement max file count when available.
	}

	private DocumentRequirement findRequirement(Long requirementId) {
		if (requirementId == null) {
			return null;
		}
		return requirementRepository.findById(requirementId).orElse(null);
	}
}
