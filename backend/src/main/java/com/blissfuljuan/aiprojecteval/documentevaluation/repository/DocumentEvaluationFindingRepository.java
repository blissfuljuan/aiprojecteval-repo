package com.blissfuljuan.aiprojecteval.documentevaluation.repository;

import com.blissfuljuan.aiprojecteval.documentevaluation.model.DocumentEvaluationFinding;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentEvaluationFindingRepository extends JpaRepository<DocumentEvaluationFinding, Long> {

	List<DocumentEvaluationFinding> findByEvaluationIdOrderByDisplayOrderAsc(Long evaluationId);
}
