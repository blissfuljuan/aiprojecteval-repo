package com.blissfuljuan.aiprojecteval.submission.repository;

import com.blissfuljuan.aiprojecteval.submission.enums.SubmissionStatus;
import com.blissfuljuan.aiprojecteval.submission.enums.SubmissionType;
import com.blissfuljuan.aiprojecteval.submission.model.Submission;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {

	List<Submission> findBySubmittedByIdOrderByCreatedAtDesc(Long submittedById);

	List<Submission> findByTypeAndSubmittedByIdOrderByCreatedAtDesc(
			SubmissionType type,
			Long submittedById);

	List<Submission> findByTypeAndAssignmentIdOrderByCreatedAtDesc(
			SubmissionType type,
			Long assignmentId);

	List<Submission> findByTypeAndAssignmentIdAndSubmittedByIdOrderByCreatedAtDesc(
			SubmissionType type,
			Long assignmentId,
			Long submittedById);

	List<Submission> findByTypeAndAssignmentIdAndProjectIdOrderByCreatedAtDesc(
			SubmissionType type,
			Long assignmentId,
			Long projectId);

	List<Submission> findByTypeAndAssignmentIdAndCourseClassIdOrderByCreatedAtDesc(
			SubmissionType type,
			Long assignmentId,
			Long courseClassId);

	List<Submission> findByTypeAndProjectIdOrderByCreatedAtDesc(
			SubmissionType type,
			Long projectId);

	List<Submission> findByTypeAndCourseClassIdOrderByCreatedAtDesc(
			SubmissionType type,
			Long courseClassId);

	List<Submission> findByTypeAndAssignmentIdAndRequirementIdOrderByCreatedAtDesc(
			SubmissionType type,
			Long assignmentId,
			Long requirementId);

	Optional<Submission> findFirstByTypeAndSubmittedByIdAndAssignmentIdAndRequirementIdAndStatus(
			SubmissionType type,
			Long submittedById,
			Long assignmentId,
			Long requirementId,
			SubmissionStatus status);

	@Query("""
			select coalesce(max(submission.attemptNumber), 0)
			from Submission submission
			where submission.submittedBy.id = :submittedById
				and submission.type = :type
				and submission.assignmentId = :assignmentId
				and submission.requirementId = :requirementId
			""")
	Integer findMaxAttemptNumber(
			@Param("type") SubmissionType type,
			@Param("submittedById") Long submittedById,
			@Param("assignmentId") Long assignmentId,
			@Param("requirementId") Long requirementId);
}
