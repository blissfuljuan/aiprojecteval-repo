package com.blissfuljuan.aiprojecteval.documentevaluation.repository;

import com.blissfuljuan.aiprojecteval.documentevaluation.enums.ConfigurationStatus;
import com.blissfuljuan.aiprojecteval.documentevaluation.model.EvaluationRubric;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EvaluationRubricRepository extends JpaRepository<EvaluationRubric, Long> {

	List<EvaluationRubric> findByStatus(ConfigurationStatus status);

	List<EvaluationRubric> findByOwnerInstructorId(Long ownerInstructorId);
}
