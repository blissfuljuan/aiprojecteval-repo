package com.blissfuljuan.aiprojecteval.submission.service;

import com.blissfuljuan.aiprojecteval.common.exception.ResourceNotFoundException;
import com.blissfuljuan.aiprojecteval.submission.dto.response.SubmissionResponse;
import com.blissfuljuan.aiprojecteval.submission.dto.response.SubmissionSummaryResponse;
import com.blissfuljuan.aiprojecteval.submission.enums.SubmissionType;
import com.blissfuljuan.aiprojecteval.submission.mapper.SubmissionMapper;
import com.blissfuljuan.aiprojecteval.submission.model.Submission;
import com.blissfuljuan.aiprojecteval.submission.repository.SubmissionRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class SubmissionQueryServiceImpl implements SubmissionQueryService {

	private final SubmissionRepository submissionRepository;

	SubmissionQueryServiceImpl(SubmissionRepository submissionRepository) {
		this.submissionRepository = submissionRepository;
	}

	@Override
	@Transactional(readOnly = true)
	public SubmissionResponse getSubmissionById(Long submissionId) {
		return SubmissionMapper.toResponse(getSubmissionEntityById(submissionId));
	}

	@Override
	@Transactional(readOnly = true)
	public Submission getSubmissionEntityById(Long submissionId) {
		return submissionRepository.findById(submissionId)
				.orElseThrow(() -> new ResourceNotFoundException("Submission not found"));
	}

	@Override
	@Transactional(readOnly = true)
	public List<SubmissionResponse> findSubmissionsByTypeAndAssignmentAndSubmitter(
			SubmissionType type,
			Long assignmentId,
			Long submittedById) {
		return submissionRepository
				.findByTypeAndAssignmentIdAndSubmittedByIdOrderByCreatedAtDesc(type, assignmentId, submittedById)
				.stream()
				.map(SubmissionMapper::toResponse)
				.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<SubmissionResponse> findSubmissionsByTypeAndAssignmentAndProject(
			SubmissionType type,
			Long assignmentId,
			Long projectId) {
		return submissionRepository
				.findByTypeAndAssignmentIdAndProjectIdOrderByCreatedAtDesc(type, assignmentId, projectId)
				.stream()
				.map(SubmissionMapper::toResponse)
				.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<SubmissionResponse> findSubmissionsByTypeAndAssignmentAndClass(
			SubmissionType type,
			Long assignmentId,
			Long courseClassId) {
		return submissionRepository
				.findByTypeAndAssignmentIdAndCourseClassIdOrderByCreatedAtDesc(type, assignmentId, courseClassId)
				.stream()
				.map(SubmissionMapper::toResponse)
				.toList();
	}

	@Override
	@Transactional(readOnly = true)
	public List<SubmissionSummaryResponse> findLatestSubmissionsByRequirement(
			SubmissionType type,
			Long assignmentId,
			Long requirementId) {
		return submissionRepository
				.findByTypeAndAssignmentIdAndRequirementIdOrderByCreatedAtDesc(type, assignmentId, requirementId)
				.stream()
				.map(SubmissionMapper::toSummaryResponse)
				.toList();
	}
}
