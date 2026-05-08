package com.blissfuljuan.aiprojecteval.documentevaluation.service;

import com.blissfuljuan.aiprojecteval.documentevaluation.dto.response.DocumentSubmissionFileResource;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.response.DocumentSubmissionFileResponse;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.response.FileUploadResponse;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.response.MultipleFileUploadResponse;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface DocumentSubmissionFileService {

	FileUploadResponse uploadFileToSubmission(
			String currentUserEmail,
			Long submissionId,
			MultipartFile file,
			String notes);

	MultipleFileUploadResponse uploadMultipleFilesToSubmission(
			String currentUserEmail,
			Long submissionId,
			List<MultipartFile> files);

	DocumentSubmissionFileResource downloadFile(Long fileId, String currentUserEmail);

	DocumentSubmissionFileResource viewFile(Long fileId, String currentUserEmail);

	DocumentSubmissionFileResponse removeFile(Long fileId, String currentUserEmail);

	FileUploadResponse replaceFile(Long fileId, MultipartFile file, String currentUserEmail);

	List<DocumentSubmissionFileResponse> getFilesBySubmission(
			Long submissionId,
			boolean includeInactive,
			String currentUserEmail);
}
