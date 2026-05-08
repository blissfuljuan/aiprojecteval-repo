package com.blissfuljuan.aiprojecteval.documentevaluation.repository;

import com.blissfuljuan.aiprojecteval.documentevaluation.enums.DocumentSubmissionStatus;
import com.blissfuljuan.aiprojecteval.documentevaluation.model.DocumentSubmission;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DocumentSubmissionRepository extends JpaRepository<DocumentSubmission, Long> {

	List<DocumentSubmission> findBySubmittedByIdOrderByCreatedAtDesc(Long submittedById);

	List<DocumentSubmission> findByAssignmentIdOrderByCreatedAtDesc(Long assignmentId);

	List<DocumentSubmission> findByProjectIdOrderByCreatedAtDesc(Long projectId);

	List<DocumentSubmission> findByCourseClassIdOrderByCreatedAtDesc(Long courseClassId);

	List<DocumentSubmission> findByAssignmentIdAndDocumentRequirementIdOrderByCreatedAtDesc(
			Long assignmentId,
			Long documentRequirementId);

	Optional<DocumentSubmission> findFirstBySubmittedByIdAndAssignmentIdAndDocumentRequirementIdAndStatus(
			Long submittedById,
			Long assignmentId,
			Long documentRequirementId,
			DocumentSubmissionStatus status);

	@Query("""
			select coalesce(max(submission.attemptNumber), 0)
			from DocumentSubmission submission
			where submission.submittedBy.id = :submittedById
				and submission.assignment.id = :assignmentId
				and submission.documentRequirement.id = :documentRequirementId
			""")
	Integer findMaxAttemptNumber(
			@Param("submittedById") Long submittedById,
			@Param("assignmentId") Long assignmentId,
			@Param("documentRequirementId") Long documentRequirementId);
}
