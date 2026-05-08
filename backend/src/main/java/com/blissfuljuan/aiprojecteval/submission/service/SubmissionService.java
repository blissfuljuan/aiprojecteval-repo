package com.blissfuljuan.aiprojecteval.submission.service;

import com.blissfuljuan.aiprojecteval.submission.dto.request.CreateSubmissionDraftRequest;
import com.blissfuljuan.aiprojecteval.submission.dto.request.CreateSubmissionRequest;
import com.blissfuljuan.aiprojecteval.submission.dto.request.SubmissionFileMetadataRequest;
import com.blissfuljuan.aiprojecteval.submission.dto.request.UpdateSubmissionDraftRequest;
import com.blissfuljuan.aiprojecteval.submission.dto.response.SubmissionFileResponse;
import com.blissfuljuan.aiprojecteval.submission.dto.response.SubmissionResponse;
import com.blissfuljuan.aiprojecteval.submission.dto.response.SubmissionSummaryResponse;
import com.blissfuljuan.aiprojecteval.submission.enums.SubmissionStatus;
import java.util.List;

public interface SubmissionService {

	SubmissionResponse createDraftSubmission(
			String currentUserEmail,
			CreateSubmissionDraftRequest request);

	SubmissionResponse createSubmission(
			String currentUserEmail,
			CreateSubmissionRequest request);

	SubmissionResponse updateDraftSubmission(
			String currentUserEmail,
			Long submissionId,
			UpdateSubmissionDraftRequest request);

	SubmissionResponse submitDraftSubmission(String currentUserEmail, Long submissionId);

	List<SubmissionSummaryResponse> getMySubmissions(
			String currentUserEmail,
			Long assignmentId,
			Long documentRequirementId,
			SubmissionStatus status,
			Long projectId,
			Long courseClassId);

	SubmissionResponse getSubmissionById(String currentUserEmail, Long submissionId);

	List<SubmissionSummaryResponse> getSubmissionsByAssignment(
			String currentUserEmail,
			Long assignmentId,
			Long documentRequirementId,
			SubmissionStatus status,
			Long submittedById,
			Long projectId,
			Long courseClassId);

	List<SubmissionSummaryResponse> getSubmissionsByProject(
			String currentUserEmail,
			Long projectId,
			Long documentRequirementId,
			SubmissionStatus status,
			Long submittedById);

	List<SubmissionSummaryResponse> getSubmissionsByClass(
			String currentUserEmail,
			Long courseClassId,
			Long documentRequirementId,
			SubmissionStatus status,
			Long submittedById);

	SubmissionResponse archiveSubmission(String currentUserEmail, Long submissionId);

	SubmissionResponse addFileMetadataToDraftSubmission(
			String currentUserEmail,
			Long submissionId,
			SubmissionFileMetadataRequest request);

	SubmissionFileResponse removeFileMetadataFromDraftSubmission(String currentUserEmail, Long fileId);
}
