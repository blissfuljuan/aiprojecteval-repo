package com.blissfuljuan.aiprojecteval.documentevaluation.repository;

import com.blissfuljuan.aiprojecteval.documentevaluation.enums.DocumentEvaluationStatus;
import com.blissfuljuan.aiprojecteval.documentevaluation.model.DocumentEvaluation;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentEvaluationRepository extends JpaRepository<DocumentEvaluation, Long> {

	Optional<DocumentEvaluation> findByIdAndStatusNot(Long id, DocumentEvaluationStatus status);

	Optional<DocumentEvaluation> findBySubmissionId(Long submissionId);

	Optional<DocumentEvaluation> findBySubmissionIdAndStatusNot(Long submissionId, DocumentEvaluationStatus status);

	Optional<DocumentEvaluation> findBySubmissionIdAndSubmittedByIdAndPublishedTrueAndStatusIn(
			Long submissionId,
			Long submittedById,
			List<DocumentEvaluationStatus> statuses);

	List<DocumentEvaluation> findByAssignmentIdOrderByCreatedAtDesc(Long assignmentId);

	List<DocumentEvaluation> findBySubmittedByIdOrderByCreatedAtDesc(Long submittedById);

	List<DocumentEvaluation> findBySubmittedByIdAndPublishedTrueAndStatusInOrderByPublishedAtDesc(
			Long submittedById,
			List<DocumentEvaluationStatus> statuses);

	List<DocumentEvaluation> findByAssignmentIdAndSubmittedByIdAndPublishedTrueAndStatusInOrderByPublishedAtDesc(
			Long assignmentId,
			Long submittedById,
			List<DocumentEvaluationStatus> statuses);

	List<DocumentEvaluation> findByProjectIdAndSubmittedByIdAndPublishedTrueAndStatusInOrderByPublishedAtDesc(
			Long projectId,
			Long submittedById,
			List<DocumentEvaluationStatus> statuses);

	List<DocumentEvaluation> findByAssignmentIdAndPublishedTrueAndStatusInOrderByPublishedAtDesc(
			Long assignmentId,
			List<DocumentEvaluationStatus> statuses);

	List<DocumentEvaluation> findByEvaluatedByIdOrderByCreatedAtDesc(Long evaluatedById);

	List<DocumentEvaluation> findByProjectIdOrderByCreatedAtDesc(Long projectId);

	List<DocumentEvaluation> findByCourseClassIdOrderByCreatedAtDesc(Long courseClassId);

	boolean existsBySubmissionIdAndStatusNot(Long submissionId, DocumentEvaluationStatus status);
}
