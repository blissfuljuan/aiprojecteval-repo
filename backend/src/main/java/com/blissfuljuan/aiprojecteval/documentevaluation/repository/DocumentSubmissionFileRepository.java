package com.blissfuljuan.aiprojecteval.documentevaluation.repository;

import com.blissfuljuan.aiprojecteval.documentevaluation.model.DocumentSubmissionFile;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentSubmissionFileRepository extends JpaRepository<DocumentSubmissionFile, Long> {

	List<DocumentSubmissionFile> findBySubmissionId(Long submissionId);
}
