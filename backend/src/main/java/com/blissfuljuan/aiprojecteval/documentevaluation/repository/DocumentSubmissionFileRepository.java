package com.blissfuljuan.aiprojecteval.documentevaluation.repository;

import com.blissfuljuan.aiprojecteval.documentevaluation.model.DocumentSubmissionFile;
import com.blissfuljuan.aiprojecteval.documentevaluation.enums.DocumentSubmissionFileStatus;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentSubmissionFileRepository extends JpaRepository<DocumentSubmissionFile, Long> {

	List<DocumentSubmissionFile> findBySubmissionId(Long submissionId);

	List<DocumentSubmissionFile> findBySubmissionIdOrderByCreatedAtAsc(Long submissionId);

	List<DocumentSubmissionFile> findBySubmissionIdAndFileStatusInOrderByCreatedAtAsc(
			Long submissionId,
			Collection<DocumentSubmissionFileStatus> statuses);

	long countBySubmissionIdAndFileStatusIn(
			Long submissionId,
			Collection<DocumentSubmissionFileStatus> statuses);
}
