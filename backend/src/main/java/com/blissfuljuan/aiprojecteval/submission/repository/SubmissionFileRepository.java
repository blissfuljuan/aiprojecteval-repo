package com.blissfuljuan.aiprojecteval.submission.repository;

import com.blissfuljuan.aiprojecteval.submission.model.SubmissionFile;
import com.blissfuljuan.aiprojecteval.submission.enums.SubmissionFileStatus;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubmissionFileRepository extends JpaRepository<SubmissionFile, Long> {

	List<SubmissionFile> findBySubmissionId(Long submissionId);

	List<SubmissionFile> findBySubmissionIdOrderByCreatedAtAsc(Long submissionId);

	List<SubmissionFile> findBySubmissionIdAndFileStatusInOrderByCreatedAtAsc(
			Long submissionId,
			Collection<SubmissionFileStatus> statuses);

	long countBySubmissionIdAndFileStatusIn(
			Long submissionId,
			Collection<SubmissionFileStatus> statuses);
}
