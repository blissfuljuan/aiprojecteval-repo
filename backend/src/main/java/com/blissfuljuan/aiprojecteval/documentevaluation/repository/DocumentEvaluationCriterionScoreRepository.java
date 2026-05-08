package com.blissfuljuan.aiprojecteval.documentevaluation.repository;

import com.blissfuljuan.aiprojecteval.documentevaluation.model.DocumentEvaluationCriterionScore;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentEvaluationCriterionScoreRepository
		extends JpaRepository<DocumentEvaluationCriterionScore, Long> {

	List<DocumentEvaluationCriterionScore> findByEvaluationIdOrderByDisplayOrderAsc(Long evaluationId);

	Optional<DocumentEvaluationCriterionScore> findByEvaluationIdAndCriterionId(Long evaluationId, Long criterionId);
}
