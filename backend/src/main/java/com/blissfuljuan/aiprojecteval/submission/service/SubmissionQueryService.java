package com.blissfuljuan.aiprojecteval.submission.service;

import com.blissfuljuan.aiprojecteval.submission.dto.response.SubmissionResponse;
import com.blissfuljuan.aiprojecteval.submission.dto.response.SubmissionSummaryResponse;
import com.blissfuljuan.aiprojecteval.submission.enums.SubmissionType;
import com.blissfuljuan.aiprojecteval.submission.model.Submission;
import java.util.List;

public interface SubmissionQueryService {

	SubmissionResponse getSubmissionById(Long submissionId);

	Submission getSubmissionEntityById(Long submissionId);

	List<SubmissionResponse> findSubmissionsByTypeAndAssignmentAndSubmitter(
			SubmissionType type,
			Long assignmentId,
			Long submittedById);

	List<SubmissionResponse> findSubmissionsByTypeAndAssignmentAndProject(
			SubmissionType type,
			Long assignmentId,
			Long projectId);

	List<SubmissionResponse> findSubmissionsByTypeAndAssignmentAndClass(
			SubmissionType type,
			Long assignmentId,
			Long courseClassId);

	List<SubmissionSummaryResponse> findLatestSubmissionsByRequirement(
			SubmissionType type,
			Long assignmentId,
			Long requirementId);
}
