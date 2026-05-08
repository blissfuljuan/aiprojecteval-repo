package com.blissfuljuan.aiprojecteval.submission.service;

import com.blissfuljuan.aiprojecteval.submission.dto.response.SubmissionFileResource;
import com.blissfuljuan.aiprojecteval.submission.dto.response.SubmissionFileResponse;
import com.blissfuljuan.aiprojecteval.submission.dto.response.FileUploadResponse;
import com.blissfuljuan.aiprojecteval.submission.dto.response.MultipleFileUploadResponse;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface SubmissionFileService {

	FileUploadResponse uploadFileToSubmission(
			String currentUserEmail,
			Long submissionId,
			MultipartFile file,
			String notes);

	MultipleFileUploadResponse uploadMultipleFilesToSubmission(
			String currentUserEmail,
			Long submissionId,
			List<MultipartFile> files);

	SubmissionFileResource downloadFile(Long fileId, String currentUserEmail);

	SubmissionFileResource viewFile(Long fileId, String currentUserEmail);

	SubmissionFileResponse removeFile(Long fileId, String currentUserEmail);

	FileUploadResponse replaceFile(Long fileId, MultipartFile file, String currentUserEmail);

	List<SubmissionFileResponse> getFilesBySubmission(
			Long submissionId,
			boolean includeInactive,
			String currentUserEmail);
}
