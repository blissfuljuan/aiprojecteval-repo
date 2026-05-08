package com.blissfuljuan.aiprojecteval.documentevaluation.service;

import com.blissfuljuan.aiprojecteval.common.exception.BadRequestException;
import com.blissfuljuan.aiprojecteval.documentevaluation.config.DocumentEvaluationStorageProperties;
import com.blissfuljuan.aiprojecteval.documentevaluation.enums.AllowedFileType;
import com.blissfuljuan.aiprojecteval.documentevaluation.enums.DocumentSubmissionFileStatus;
import com.blissfuljuan.aiprojecteval.documentevaluation.enums.DocumentSubmissionStatus;
import com.blissfuljuan.aiprojecteval.documentevaluation.model.DocumentRequirement;
import com.blissfuljuan.aiprojecteval.documentevaluation.model.DocumentSubmission;
import com.blissfuljuan.aiprojecteval.documentevaluation.repository.DocumentSubmissionFileRepository;
import com.blissfuljuan.aiprojecteval.documentevaluation.util.FileNameSanitizer;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
class DocumentSubmissionFileValidator {

	private static final List<DocumentSubmissionFileStatus> ACTIVE_FILE_STATUSES =
			List.of(DocumentSubmissionFileStatus.UPLOADED, DocumentSubmissionFileStatus.PENDING_UPLOAD);

	private final DocumentEvaluationStorageProperties storageProperties;
	private final DocumentSubmissionFileRepository fileRepository;

	DocumentSubmissionFileValidator(
			DocumentEvaluationStorageProperties storageProperties,
			DocumentSubmissionFileRepository fileRepository) {
		this.storageProperties = storageProperties;
		this.fileRepository = fileRepository;
	}

	void validateUpload(DocumentSubmission submission, MultipartFile file, int filesToUpload) {
		validateEditableSubmission(submission);
		validateFile(file, submission.getDocumentRequirement());
		validateMaxFileCount(submission, filesToUpload);
	}

	void validateEditableSubmission(DocumentSubmission submission) {
		if (submission.getStatus() == DocumentSubmissionStatus.ARCHIVED) {
			throw new BadRequestException("Archived submissions are read-only");
		}
		if (submission.getStatus() != DocumentSubmissionStatus.DRAFT) {
			throw new BadRequestException("Only draft submissions can accept file changes");
		}
	}

	void validateReadableUploadedFile(com.blissfuljuan.aiprojecteval.documentevaluation.model.DocumentSubmissionFile file) {
		if (file.getFileStatus() != DocumentSubmissionFileStatus.UPLOADED) {
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

	private void validateMaxFileCount(DocumentSubmission submission, int filesToUpload) {
		long activeFileCount = fileRepository.countBySubmissionIdAndFileStatusIn(
				submission.getId(),
				ACTIVE_FILE_STATUSES);
		// TODO: compare activeFileCount + filesToUpload against a per-requirement max file count when available.
	}
}
