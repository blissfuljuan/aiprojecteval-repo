package com.blissfuljuan.aiprojecteval.documentevaluation.service;

import com.blissfuljuan.aiprojecteval.documentevaluation.dto.request.CreateDocumentSubmissionDraftRequest;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.request.CreateDocumentSubmissionRequest;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.request.DocumentSubmissionFileMetadataRequest;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.request.UpdateDocumentSubmissionDraftRequest;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.response.DocumentSubmissionFileResponse;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.response.DocumentSubmissionResponse;
import com.blissfuljuan.aiprojecteval.documentevaluation.dto.response.DocumentSubmissionSummaryResponse;
import com.blissfuljuan.aiprojecteval.documentevaluation.enums.DocumentSubmissionStatus;
import java.util.List;

public interface DocumentSubmissionService {

	DocumentSubmissionResponse createDraftSubmission(
			String currentUserEmail,
			CreateDocumentSubmissionDraftRequest request);

	DocumentSubmissionResponse createSubmission(
			String currentUserEmail,
			CreateDocumentSubmissionRequest request);

	DocumentSubmissionResponse updateDraftSubmission(
			String currentUserEmail,
			Long submissionId,
			UpdateDocumentSubmissionDraftRequest request);

	DocumentSubmissionResponse submitDraftSubmission(String currentUserEmail, Long submissionId);

	List<DocumentSubmissionSummaryResponse> getMySubmissions(
			String currentUserEmail,
			Long assignmentId,
			Long documentRequirementId,
			DocumentSubmissionStatus status,
			Long projectId,
			Long courseClassId);

	DocumentSubmissionResponse getSubmissionById(String currentUserEmail, Long submissionId);

	List<DocumentSubmissionSummaryResponse> getSubmissionsByAssignment(
			String currentUserEmail,
			Long assignmentId,
			Long documentRequirementId,
			DocumentSubmissionStatus status,
			Long submittedById,
			Long projectId,
			Long courseClassId);

	List<DocumentSubmissionSummaryResponse> getSubmissionsByProject(
			String currentUserEmail,
			Long projectId,
			Long documentRequirementId,
			DocumentSubmissionStatus status,
			Long submittedById);

	List<DocumentSubmissionSummaryResponse> getSubmissionsByClass(
			String currentUserEmail,
			Long courseClassId,
			Long documentRequirementId,
			DocumentSubmissionStatus status,
			Long submittedById);

	DocumentSubmissionResponse archiveSubmission(String currentUserEmail, Long submissionId);

	DocumentSubmissionResponse addFileMetadataToDraftSubmission(
			String currentUserEmail,
			Long submissionId,
			DocumentSubmissionFileMetadataRequest request);

	DocumentSubmissionFileResponse removeFileMetadataFromDraftSubmission(String currentUserEmail, Long fileId);
}
